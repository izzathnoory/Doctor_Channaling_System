import java.util.ArrayList;

public class PatientManager {

    public PatientManager(int historySize) {
        DataStore.initHistorySize(historySize);
    }

    // ===== REGISTER IN THE SYSTEM =====
    public void registerPatient(String id, String name, String mobile, String email, String city, int age, String medicalHistory, String password) {
        if (DataStore.patients.containsKey(id)) {
            System.out.println("Patient already registered!");
            return;
        }
        Patient patient = new Patient(id, name, mobile, email, city, age, medicalHistory, password);
        DataStore.patients.put(id, patient);
        NotificationService.sendNotification("\n✓ PATIENT REGISTRATION SUCCESSFUL: " + name + " (ID: " + id + ")");
    }

    // ===== SEARCH AVAILABLE DOCTORS BY SPECIALIZATION =====
    public void searchDoctorsBySpecialization(String specialization) {
        DoctorManager docMgr = new DoctorManager();
        docMgr.searchDoctor(specialization);
    }

    // ===== VIEW DOCTOR DETAILS (name, available time slots, consultation fee) =====
    public void viewDoctorDetails(String doctorId) {
        DoctorManager docMgr = new DoctorManager();
        docMgr.viewDoctorDetails(doctorId);
    }

    public void displayDoctorListBySpecialization(String specialization) {
        System.out.println("\n--- Searching for doctors in: " + specialization + " ---");
        DoctorManager docMgr = new DoctorManager();
        ArrayList<Doctor> doctors = docMgr.getDoctorsBySpecialization(specialization);
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors found in this specialization.");
        } else {
            System.out.println("Found " + doctors.size() + " doctor(s):");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i));
            }
        }
    }

    public void bookAppointment(String patientId, String doctorId, String appointmentDateTime) {
        Patient patient = DataStore.patients.get(patientId);
        Doctor doctor = DataStore.doctors.get(doctorId);
        
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        
        String appointmentNumber = DataStore.generateAppointmentNumber();
        String bookedTime = java.time.Instant.now().toString();
        String appointmentInfo = patientId + "-" + doctorId + "-" + appointmentDateTime + "|" + appointmentNumber + "|" + bookedTime;

        boolean booked = DataStore.bookAppointmentWithWaitingList(appointmentInfo, doctorId);
        saveAppointmentHistory(appointmentInfo);
        DataStore.saveAll();
        
        if (booked) {
            System.out.println("\n✓ APPOINTMENT BOOKED SUCCESSFULLY!");
            System.out.println("Status: CONFIRMED");
            NotificationService.sendNotification("APPOINTMENT BOOKED SUCCESSFULLY!" +
                    "Patient: " + patient.getName() + "\n" +
                    "Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")\n" +
                    "Date & Time: " + appointmentDateTime + "\n" +
                    "Status: CONFIRMED\n" +
                    "Consultation Fee: Rs. " + doctor.getConsultationFee());
        } else {
            int waitingPosition = DataStore.getWaitingListCount(doctorId);
            System.out.println("\n⏳ APPOINTMENT ADDED TO WAITING LIST");
            System.out.println("Doctor " + doctor.getName() + " is fully booked.");
            System.out.println("Status: WAITING LIST");
            System.out.println("Your position in waiting list: " + waitingPosition);
            NotificationService.sendNotification("⏳ APPOINTMENT ADDED TO WAITING LIST" +
                    "Patient: " + patient.getName() + "\n" +
                    "Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")\n" +
                    "Date & Time: " + appointmentDateTime + "\n" +
                    "Status: WAITING LIST\n" +
                    "Position: #" + waitingPosition + "\n" +
                    "You will be notified when a slot becomes available.");
        }
    }

    public void viewAllScheduledAppointments(String patientId) {
        System.out.println("\n--- Your Scheduled Appointments ---");
        System.out.println("Patient ID: " + patientId);
        System.out.println("\nNote: All booked appointments:");
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            for (String apt : DataStore.appointments.toList()) {
                System.out.println(apt);
            }
        }
    }

    public void cancelAppointment(String patientId) {
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No appointments to cancel.");
            return;
        }
        String removed = DataStore.appointments.dequeue();
        String cancelTime = java.time.Instant.now().toString();

        String[] parts = removed.split("-");
        String doctorId = null;
        if (parts.length >= 2) {
            doctorId = parts[1];
        }
        
        DataStore.addCancelledAppointment(removed);

        DataStore.appointmentHistory.push(removed);

        if (doctorId != null) {
            int current = DataStore.doctorCurrentAppointments.getOrDefault(doctorId, 0);
            DataStore.doctorCurrentAppointments.put(doctorId, Math.max(0, current - 1));
        }
        DataStore.saveAll();
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              APPOINTMENT CANCELLATION DETAILS                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("✓ Appointment Cancelled Successfully");
        System.out.println("\n--- CANCELLED APPOINTMENT ---");
        System.out.println("Appointment: " + removed);
        System.out.println("Cancelled Time: " + cancelTime);
        System.out.println("Cancelled by Patient: " + patientId);
        
        
        if (doctorId != null && DataStore.getWaitingListCount(doctorId) > 0) {
            WaitingQueue<String> waitingList = DataStore.getWaitingList(doctorId);
            if (!waitingList.isEmpty()) {
                String promoted = waitingList.dequeue();
                DataStore.appointments.enqueue(promoted);
                DataStore.saveAll();
                System.out.println("\n--- WAITING LIST PROMOTION ---");
                System.out.println("✓ Next patient promoted from waiting list!");
                System.out.println("Promoted Appointment: " + promoted);
                NotificationService.sendNotification("✓ Your appointment has been confirmed!" +
                        "Appointment: " + promoted + "\n" +
                        "You were promoted from the waiting list.");
            }
        }
        
        NotificationService.sendNotification("✓ APPOINTMENT CANCELLED" +
                "Appointment: " + removed + "\n" +
                "Cancelled Time: " + cancelTime);
    }

    public void requestReschedule(String patientId, String appointmentId, String newDateTime) {
        System.out.println("\n--- Reschedule Request Submitted ---");
        String rescheduleRequest = patientId + "-" + appointmentId + "-NewTime:" + newDateTime;
        System.out.println("Added to reschedule queue: " + rescheduleRequest);
        NotificationService.sendNotification("✓ Reschedule request submitted for appointment: " + appointmentId + 
                "\nRequested new time: " + newDateTime + "\nYou will receive confirmation shortly.");
    }

    // ===== VIEW PATIENT PROFILE =====
    public void viewPatientProfile(String patientId) {
        Patient patient = getPatient(patientId);
        if (patient != null) {
            patient.displayProfile();
        } else {
            System.out.println("Patient not found.");
        }
    }

    // Get patient by ID
    public Patient getPatient(String id) {
        return DataStore.patients.get(id);
    }

    // Save appointment history
    public void saveAppointmentHistory(String appointmentInfo) {
        DataStore.appointmentHistory.push(appointmentInfo);
    }

    // Display appointment history
    public void displayHistory() {
        System.out.println("\n--- Your Appointment History ---");
        if (DataStore.appointmentHistory.isEmpty()) {
            System.out.println("No appointment history.");
        } else {
            for (String apt : DataStore.appointmentHistory.toList()) {
                System.out.println(apt);
            }
        }
    }
    
    // ===== VIEW ALL AVAILABLE SCHEDULES =====
    public void viewAllAvailableSchedules() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              ALL AVAILABLE DOCTOR SCHEDULES                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        if (DataStore.doctors.isEmpty()) {
            System.out.println("No doctors available in the system.");
            return;
        }
        
        for (Doctor doc : DataStore.doctors.values()) {
            int booked = DataStore.getAppointmentCount(doc.getId());
            int limit = DataStore.doctorSlotLimits.getOrDefault(doc.getId(), 5);
            int available = limit - booked;
            
            System.out.println("\nDoctor: " + doc.getName() + " (" + doc.getSpecialization() + ")");
            System.out.println("  ID: " + doc.getId());
            System.out.println("  Consultation Fee: Rs. " + doc.getConsultationFee());
            if (doc.getAvailableSlots() != null && !doc.getAvailableSlots().isEmpty()) {
                System.out.println("  Schedule: " + doc.getAvailableSlots() + " (Max: " + limit + " appointments)");
            }
            System.out.println("  Availability: " + available + " available out of " + limit);
        }
    }
    
    // ===== VIEW MY APPOINTMENT HISTORY =====
    public void viewMyAppointmentHistory(String patientId) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 MY APPOINTMENT HISTORY                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("Patient ID: " + patientId);
        System.out.println("\nAll appointment history:");
        if (DataStore.appointmentHistory.isEmpty()) {
            System.out.println("No appointment history.");
        } else {
            for (String apt : DataStore.appointmentHistory.toList()) {
                System.out.println(apt);
            }
        }
    }
    
    // ===== VIEW DOCTORS BY TIME SLOT =====
    public void viewDoctorsByTimeSlot(String timeSlot) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         AVAILABLE DOCTORS FOR TIME: " + String.format("%-21s", timeSlot) + "║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        if (DataStore.doctors.isEmpty()) {
            System.out.println("No doctors available in the system.");
            return;
        }
        
        boolean foundDoctor = false;
        for (Doctor doc : DataStore.doctors.values()) {
            int booked = DataStore.getAppointmentCount(doc.getId());
            int limit = DataStore.doctorSlotLimits.getOrDefault(doc.getId(), 5);
            int available = limit - booked;
            
            // Check if doctor has this time slot available
            String docSlots = doc.getAvailableSlots();
            if (docSlots != null && docSlots.toLowerCase().contains(timeSlot.toLowerCase()) && available > 0) {
                foundDoctor = true;
                System.out.println("\n✓ Doctor: " + doc.getName() + " (" + doc.getSpecialization() + ")");
                System.out.println("  ID: " + doc.getId());
                System.out.println("  Consultation Fee: Rs. " + doc.getConsultationFee());
                System.out.println("  Available Slots: " + available + " out of " + limit);
                System.out.println("  Schedule: " + doc.getAvailableSlots());
            }
        }
        
        if (!foundDoctor) {
            System.out.println("\nNo doctors available at the time slot: " + timeSlot);
            System.out.println("Please try a different time or view all schedules.");
        }
    }
    
    // ===== REQUEST HOME VISIT =====
    public void requestHomeVisit(String patientId, String location, String doctorId, String requestedDate, String requestedTime) {
        Patient patient = DataStore.patients.get(patientId);
        Doctor doctor = DataStore.doctors.get(doctorId);
        
        if (patient == null) {
            System.out.println("\n❌ ERROR: Patient not found!");
            return;
        }
        
        if (doctor == null) {
            System.out.println("\n❌ ERROR: Doctor not found!");
            return;
        }
        
        // Validate location exists in network
        if (!DataStore.locationGraph.getLocations().contains(location)) {
            System.out.println("\n❌ ERROR: Location '" + location + "' is not in our service area.");
            System.out.println("Available locations: " + DataStore.locationGraph.getLocations());
            return;
        }
        
        // Generate request ID
        String requestId = "HVR-" + System.currentTimeMillis();
        
        // Create home visit request
        HomeVisitRequest request = new HomeVisitRequest(
            requestId,
            patientId,
            patient.getName(),
            location,
            doctorId,
            requestedDate,
            requestedTime
        );
        
        // Add to queue
        DataStore.homeVisitRequests.enqueue(request);
        
        // Calculate shortest path from Hospital
        System.out.println("\n✓ HOME VISIT REQUEST SUBMITTED SUCCESSFULLY!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Request ID: " + requestId);
        System.out.println("Patient: " + patient.getName());
        System.out.println("Location: " + location);
        System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.println("Requested Date: " + requestedDate);
        System.out.println("Requested Time: " + requestedTime);
        System.out.println("Status: PENDING");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Show route calculation
        System.out.println("\n📍 ROUTE CALCULATION:");
        ShortestPathResult result = DataStore.locationGraph.dijkstra("Hospital").get(location);
        if (result != null) {
            System.out.println("Distance from Hospital: " + result.getDistance() + " km");
            System.out.println("Optimal Route: " + String.join(" → ", result.getPath()));
        }
    }
    
    // ===== VIEW HOME VISIT REQUESTS =====
    public void viewHomeVisitRequests(String patientId) {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("      YOUR HOME VISIT REQUESTS");
        System.out.println("═══════════════════════════════════════════");
        
        ArrayList<HomeVisitRequest> patientRequests = new ArrayList<>();
        for (HomeVisitRequest request : DataStore.homeVisitRequests.toList()) {
            if (request.getPatientId().equals(patientId)) {
                patientRequests.add(request);
            }
        }
        
        if (patientRequests.isEmpty()) {
            System.out.println("No home visit requests found.");
        } else {
            for (int i = 0; i < patientRequests.size(); i++) {
                System.out.println("\n" + (i + 1) + ". " + patientRequests.get(i).getDisplayString());
            }
        }
    }

    public void bubbleSortPatientsByAge() {
    ArrayList<Patient> patients = new ArrayList<>(DataStore.patients.values());

    if (patients.size() <= 1) {
        System.out.println("Not enough patients to sort.");
        return;
    }

    for (int i = 0; i < patients.size() - 1; i++) {
        for (int j = 0; j < patients.size() - i - 1; j++) {
            if (patients.get(j).getAge() > patients.get(j + 1).getAge()) {
                Patient temp = patients.get(j);
                patients.set(j, patients.get(j + 1));
                patients.set(j + 1, temp);
            }
        }
    }

    System.out.println("\n--- Patients Sorted by Age (Bubble Sort) ---");
    for (Patient p : patients) {
        System.out.println(p);
    }
}


    public void insertionSortPatientsByAge() {
    ArrayList<Patient> patients = new ArrayList<>(DataStore.patients.values());

    if (patients.size() <= 1) {
        System.out.println("Not enough patients to sort.");
        return;
    }

    for (int i = 1; i < patients.size(); i++) {
        Patient key = patients.get(i);
        int j = i - 1;

        while (j >= 0 && patients.get(j).getAge() > key.getAge()) {
            patients.set(j + 1, patients.get(j));
            j--;
        }
        patients.set(j + 1, key);
    }

    System.out.println("\n--- Patients Sorted by Age (Insertion Sort) ---");
    for (Patient p : patients) {
        System.out.println(p);
    }
}

}

