import java.util.HashMap;

public class ReceptionistManager {

    private DoctorManager doctorManager;
    private PatientManager patientManager;
    private Appointment appointmentManager;
    
    public ReceptionistManager() {
        this.doctorManager = new DoctorManager();
        this.patientManager = new PatientManager(50);
        this.appointmentManager = new Appointment();
    }

    // ===== REGISTER DOCTORS IN THE SYSTEM =====
    public void registerDoctor(String id, String name, String specialization, int consultationFee) {
        doctorManager.registerDoctor(id, name, specialization, consultationFee);
    }

    public void registerDoctorWithAvailability(String id, String name, String specialization, int consultationFee, String availableSlots) {
        if (DataStore.doctors.containsKey(id)) {
            System.out.println("Doctor already exists!");
            return;
        }
        Doctor doctor = new Doctor(id, name, specialization, consultationFee, availableSlots);
        DataStore.doctors.put(id, doctor);
        DataStore.saveAll();
        NotificationService.sendNotification("\n✓ DOCTOR REGISTERED SUCCESSFULLY\n" +
                "Name: " + name + "\n" +
                "Specialization: " + specialization + "\n" +
                "Consultation Fee: Rs. " + consultationFee + "\n" +
                "Available Slots: " + availableSlots);
    }

    // ===== UPDATE DOCTOR AVAILABILITY AND TIME SLOTS =====
    public void updateDoctorAvailability(String doctorId, String newAvailableSlots) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor != null) {
            String oldSlots = doctor.getAvailableSlots();
            doctor.setAvailableSlots(newAvailableSlots);
            DataStore.saveAll();
            System.out.println("\n--- Doctor Availability Updated ---");
            System.out.println("Doctor: " + doctor.getName());
            System.out.println("Previous Slots: " + oldSlots);
            System.out.println("New Slots: " + newAvailableSlots);
            NotificationService.sendNotification("✓ Doctor availability updated\n" +
                    "Doctor: " + doctor.getName() + "\n" +
                    "New Available Slots: " + newAvailableSlots);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    public void updateDoctorDetails(String doctorId, String name, String specialization, int fee, String slots) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setConsultationFee(fee);
        doctor.setAvailableSlots(slots);
        DataStore.saveAll();
        NotificationService.sendNotification("✓ Doctor details updated: " + doctorId);
    }

    public void deleteDoctor(String doctorId) {
        if (DataStore.doctors.remove(doctorId) != null) {
            DataStore.saveAll();
            System.out.println("Doctor deleted: " + doctorId);
            NotificationService.sendNotification("✓ Doctor deleted: " + doctorId);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    // View doctor's current availability
    public void viewDoctorAvailability(String doctorId) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor != null) {
            System.out.println("\n--- Doctor Availability ---");
            System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            System.out.println("Available Slots: " + doctor.getAvailableSlots());
        } else {
            System.out.println("Doctor not found.");
        }
    }

    // ===== REGISTER PATIENTS (if required) =====
    public void registerPatient(String id, String name, String mobile, String email, String city, int age, String medicalHistory, String password) {
        patientManager.registerPatient(id, name, mobile, email, city, age, medicalHistory, password);
    }

    public void updatePatientDetails(String patientId, String name, String mobile, String email, String city, int age, String medicalHistory, String password) {
        Patient patient = DataStore.patients.get(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        patient.setName(name);
        patient.setMobile(mobile);
        patient.setEmail(email);
        patient.setCity(city);
        patient.setAge(age);
        patient.setMedicalHistory(medicalHistory);
        patient.setPassword(password);
        DataStore.saveAll();
        NotificationService.sendNotification("✓ Patient details updated: " + patientId);
    }

    public void deletePatient(String patientId) {
        if (DataStore.patients.remove(patientId) != null) {
            DataStore.saveAll();
            System.out.println("Patient deleted: " + patientId);
            NotificationService.sendNotification("✓ Patient deleted: " + patientId);
        } else {
            System.out.println("Patient not found.");
        }
    }

    public void viewPatientDetails(String patientId) {
        Patient patient = DataStore.patients.get(patientId);
        if (patient != null) {
            patient.displayProfile();
        } else {
            System.out.println("Patient not found.");
        }
    }

    // ===== VIEW ALL SCHEDULED APPOINTMENTS =====
    public void viewAllScheduledAppointments() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          ALL SCHEDULED APPOINTMENTS (FIFO ORDER)             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No appointments.");
        } else {
            for (String apt : DataStore.appointments.toList()) {
                System.out.println(apt);
            }
        }
    }

    // View appointments by doctor
    public void viewAppointmentsByDoctor(String doctorId) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("\n--- Appointments for Dr. " + doctor.getName() + " ---");
        System.out.println("(Note: Showing all appointments from queue)");
        // In a real system, this would filter by doctorId
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No appointments.");
        } else {
            for (String apt : DataStore.appointments.toList()) {
                System.out.println(apt);
            }
        }
    }

    // ===== MANAGE APPOINTMENT CANCELLATIONS =====
    public void cancelAppointment() {
        String removed = DataStore.appointments.isEmpty() ? null : DataStore.appointments.dequeue();
        if (removed != null) {
            String cancelTime = java.time.Instant.now().toString();
            DataStore.addCancelledAppointment(removed);
            DataStore.appointmentHistory.push(removed);
            DataStore.saveAll();
            
            // Display cancellation details
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              APPOINTMENT CANCELLATION DETAILS                 ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("✓ Appointment Cancelled Successfully");
            System.out.println("\n--- CANCELLED APPOINTMENT ---");
            System.out.println("Appointment: " + removed);
            System.out.println("Cancelled Time: " + cancelTime);
            System.out.println("Cancelled by: Receptionist");
            
            NotificationService.sendNotification("\n✓ APPOINTMENT CANCELLED\n" +
                    "Details: " + removed + "\n" +
                    "Cancelled Time: " + cancelTime + "\n" +
                    "Patient & Doctor have been notified.");
        } else {
            System.out.println("No appointments to cancel.");
        }
    }

    // Cancel appointment and provide reason
    public void cancelAppointmentWithReason(String appointmentId, String reason) {
        String removed = DataStore.appointments.isEmpty() ? null : DataStore.appointments.dequeue();
        if (removed != null) {
            String cancelTime = java.time.Instant.now().toString();
            DataStore.addCancelledAppointment(removed + " (Reason: " + reason + ")");
            DataStore.saveAll();
            
            // Display cancellation details
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              APPOINTMENT CANCELLATION DETAILS                 ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("✓ Appointment Cancelled Successfully");
            System.out.println("\n--- CANCELLED APPOINTMENT ---");
            System.out.println("Appointment: " + removed);
            System.out.println("Cancelled Time: " + cancelTime);
            System.out.println("Cancellation Reason: " + reason);
            System.out.println("Cancelled by: Receptionist");
            
            NotificationService.sendNotification("\n✓ APPOINTMENT CANCELLED\n" +
                    "Details: " + removed + "\n" +
                    "Reason: " + reason + "\n" +
                    "Cancelled Time: " + cancelTime + "\n" +
                    "Patient & Doctor have been notified.");
        } else {
            System.out.println("No appointments to cancel.");
        }
    }

    // ===== ASSIGN APPOINTMENTS FROM THE WAITING LIST =====
    public void assignRescheduleRequest(String rescheduleRequestId) {
        System.out.println("Reschedule request assigned: " + rescheduleRequestId);
    }

    public void approveRescheduleRequest(int requestIndex) {
        System.out.println("Reschedule request approved.");
        NotificationService.sendNotification("✓ Reschedule request approved.");
    }

    public void rejectRescheduleRequest(int requestIndex, String reason) {
        System.out.println("Reschedule request rejected. Reason: " + reason);
        NotificationService.sendNotification("✗ Reschedule request rejected. Reason: " + reason);
    }

    // ===== HANDLE RESCHEDULE REQUESTS =====
    public void submitRescheduleRequest(String patientId, String appointmentId, String newDateTime) {
        System.out.println("\n--- Reschedule Request Received ---");
        System.out.println("Patient: " + patientId);
        System.out.println("Appointment: " + appointmentId);
        System.out.println("New Date/Time: " + newDateTime);
        
        NotificationService.sendNotification("✓ Reschedule request received\n" +
                "Patient: " + patientId + "\n" +
                "Requested New Time: " + newDateTime + "\n" +
                "Status: Pending approval");
    }

    public void viewRescheduleQueue() {
        System.out.println("\n--- Reschedule Queue ---");
        System.out.println("No pending reschedule requests.");
    }

    // ===== ENSURE SMOOTH APPOINTMENT FLOW =====
    public void displaySystemSummary() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           HOSPITAL MANAGEMENT SYSTEM - SUMMARY               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n--- REGISTERED DOCTORS ---");
        if (DataStore.doctors.isEmpty()) {
            System.out.println("No doctors registered.");
        } else {
            System.out.println("Total Doctors: " + DataStore.doctors.size());
            for (Doctor doc : DataStore.doctors.values()) {
                System.out.println("  • " + doc);
            }
        }

        System.out.println("\n--- REGISTERED PATIENTS ---");
        if (DataStore.patients.isEmpty()) {
            System.out.println("No patients registered.");
        } else {
            System.out.println("Total Patients: " + DataStore.patients.size());
            for (Patient pat : DataStore.patients.values()) {
                System.out.println("  • " + pat);
            }
        }

        System.out.println("\n--- APPOINTMENT FLOW STATUS ---");
        System.out.println("Total Scheduled Appointments: (See queue below)");
        viewAllScheduledAppointments();

        System.out.println("\n--- PENDING RESCHEDULE REQUESTS ---");
        System.out.println("No pending reschedule requests.");
    }

    // Book appointment on behalf of patient
    public void bookAppointmentForPatient(String patientId, String doctorId, String appointmentDateTime) {
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
        
        // Use waiting list system
        boolean booked = DataStore.bookAppointmentWithWaitingList(appointmentInfo, doctorId);
        DataStore.saveAll();

        if (booked) {
            System.out.println("\n--- Appointment Booked (by Receptionist) ---");
            System.out.println("Patient: " + patient.getName());
            System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            System.out.println("Date & Time: " + appointmentDateTime);
            System.out.println("Status: CONFIRMED");
            System.out.println("Fee: Rs. " + doctor.getConsultationFee());

            NotificationService.sendNotification("\n✓ APPOINTMENT BOOKED BY RECEPTIONIST\n" +
                    "Patient: " + patient.getName() + "\n" +
                    "Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")\n" +
                    "Date & Time: " + appointmentDateTime + "\n" +
                    "Status: CONFIRMED\n" +
                    "Consultation Fee: Rs. " + doctor.getConsultationFee());
        } else {
            // Added to waiting list
            int waitingPosition = DataStore.getWaitingListCount(doctorId);
            System.out.println("\n--- Appointment Added to Waiting List (by Receptionist) ---");
            System.out.println("Patient: " + patient.getName());
            System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            System.out.println("Requested Date & Time: " + appointmentDateTime);
            System.out.println("Status: WAITING LIST");
            System.out.println("Position in queue: " + waitingPosition);

            NotificationService.sendNotification("\n⏳ APPOINTMENT ADDED TO WAITING LIST (by Receptionist)\n" +
                    "Patient: " + patient.getName() + "\n" +
                    "Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")\n" +
                    "Status: WAITING LIST\n" +
                    "Position: #" + waitingPosition + "\n" +
                    "You will be notified when a slot becomes available.");
        }
    }
    
    // View waiting list for a specific doctor
    public void viewWaitingList(String doctorId) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        
        WaitingQueue<String> waitingList = DataStore.getWaitingList(doctorId);
        int count = DataStore.getWaitingListCount(doctorId);
        int booked = DataStore.getAppointmentCount(doctorId);
        int limit = DataStore.doctorSlotLimits.getOrDefault(doctorId, 5);
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              DOCTOR APPOINTMENT STATUS                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.println("Appointment Slots:");
        System.out.println("  - Booked: " + booked + "/" + limit);
        System.out.println("  - Available: " + (limit - booked));
        System.out.println("  - Waiting List: " + count);
        
        if (count == 0) {
            System.out.println("\nNo patients waiting for this doctor.");
        } else {
            System.out.println("\n--- Patients in Waiting List ---");
            for (String apt : waitingList.toList()) {
                System.out.println(apt);
            }
        }
    }
    
    // View waiting lists for all doctors
    public void viewAllWaitingLists() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        APPOINTMENT STATUS FOR ALL DOCTORS                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        boolean hasWaiting = false;
        for (Doctor doc : DataStore.doctors.values()) {
            int booked = DataStore.getAppointmentCount(doc.getId());
            int waiting = DataStore.getWaitingListCount(doc.getId());
            int limit = DataStore.doctorSlotLimits.getOrDefault(doc.getId(), 5);
            
            System.out.println("\nDoctor: " + doc.getName() + " (" + doc.getSpecialization() + ")");
            System.out.println("  Slots: " + booked + "/" + limit + " | Waiting: " + waiting);
            
            if (waiting > 0) {
                hasWaiting = true;
            }
        }
        
        if (!hasWaiting) {
            System.out.println("\nNo patients in any waiting lists.");
        }
    }

    // Get list of available doctors
    public void listAllDoctors() {
        if (DataStore.doctors.isEmpty()) {
            System.out.println("\nNo doctors registered in the system.");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              REGISTERED DOCTORS IN SYSTEM                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        for (Doctor doc : DataStore.doctors.values()) {
            System.out.println(doc);
        }
        System.out.println("\nTotal: " + DataStore.doctors.size() + " doctor(s)");
    }

    // Get list of registered patients
    public void listAllPatients() {
        if (DataStore.patients.isEmpty()) {
            System.out.println("\nNo patients registered in the system.");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              REGISTERED PATIENTS IN SYSTEM                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        for (Patient pat : DataStore.patients.values()) {
            System.out.println(pat);
        }
        System.out.println("\nTotal: " + DataStore.patients.size() + " patient(s)");
    }
    
    // ===== SORT PATIENTS BY AGE =====
    public void sortPatientsByAge() {
        if (DataStore.patients.isEmpty()) {
            System.out.println("\nNo patients registered in the system.");
            return;
        }
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           PATIENTS SORTED BY AGE (Ascending)                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // Convert to array and sort using bubble sort
        Patient[] patients = DataStore.patients.values().toArray(Patient[]::new);
        for (int i = 0; i < patients.length - 1; i++) {
            for (int j = 0; j < patients.length - i - 1; j++) {
                if (patients[j].getAge() > patients[j + 1].getAge()) {
                    Patient temp = patients[j];
                    patients[j] = patients[j + 1];
                    patients[j + 1] = temp;
                }
            }
        }
        
        for (Patient pat : patients) {
            System.out.println(pat);
        }
        System.out.println("\nTotal: " + patients.length + " patient(s)");
    }
    
    // ===== VIEW ALL SCHEDULES =====
    public void viewAllSchedules() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              ALL DOCTOR SCHEDULES & APPOINTMENTS             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        if (DataStore.doctors.isEmpty()) {
            System.out.println("No doctors available in the system.");
            return;
        }
        
        for (Doctor doc : DataStore.doctors.values()) {
            int booked = DataStore.getAppointmentCount(doc.getId());
            int limit = DataStore.doctorSlotLimits.getOrDefault(doc.getId(), 5);
            
            System.out.println("\nDoctor: " + doc.getName() + " (" + doc.getSpecialization() + ")");
            System.out.println("  ID: " + doc.getId());
            System.out.println("  Fee: Rs. " + doc.getConsultationFee());
            if (doc.getAvailableSlots() != null && !doc.getAvailableSlots().isEmpty()) {
                System.out.println("  Schedule: " + doc.getAvailableSlots() + " (Max: " + limit + " appointments)");
            }
            System.out.println("  Appointments: " + booked + "/" + limit);
        }
    }
    
    // ===== VIEW CONFIRMED APPOINTMENTS =====
    public void viewConfirmAppointments() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CONFIRMED APPOINTMENTS (Queue)                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No confirmed appointments.");
        } else {
            for (String apt : DataStore.appointments.toList()) {
                System.out.println(apt);
            }
        }
    }
    
    // ===== VIEW FULL BOOKING HISTORY =====
    public void viewFullBookingHistory() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              FULL BOOKING HISTORY (Stack)                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        if (DataStore.appointmentHistory.isEmpty()) {
            System.out.println("No history.");
        } else {
            for (String h : DataStore.appointmentHistory.toList()) {
                System.out.println(h);
            }
        }
    }
    
    // ===== CANCEL APPOINTMENT BY ID =====
    public void cancelAppointmentById(String appointmentId) {
        // Search and cancel specific appointment
        
        String removed = null;
        java.util.List<String> allAppointments = DataStore.appointments.toList();
        
        for (String app : allAppointments) {
            if (app.contains(appointmentId)) {
                removed = app;
                break;
            }
        }
        
        if (removed != null) {
            // Recreate queue without this appointment
            DataStore.appointments = new WaitingQueue<>();
            for (String app : allAppointments) {
                if (!app.equals(removed)) {
                    DataStore.appointments.enqueue(app);
                }
            }
            
            String cancelTime = java.time.Instant.now().toString();
            DataStore.addCancelledAppointment(removed);
            DataStore.appointmentHistory.push(removed);
            DataStore.saveAll();
            
            System.out.println("\n✓ Appointment Cancelled Successfully");
            System.out.println("Appointment: " + removed);
            System.out.println("Cancelled Time: " + cancelTime);
            
            // Check for promotion from waiting list
            String[] parts = removed.split("-");
            if (parts.length >= 2) {
                String doctorId = parts[1];
                if (DataStore.getWaitingListCount(doctorId) > 0) {
                    WaitingQueue<String> wl = DataStore.getWaitingList(doctorId);
                    if (!wl.isEmpty()) {
                        String promoted = wl.dequeue();
                        DataStore.appointments.enqueue(promoted);
                        System.out.println("\n⚡ WAITING LIST PROMOTION:");
                        System.out.println("Promoted: " + promoted);
                    }
                }
            }
        } else {
            System.out.println("Appointment not found with ID: " + appointmentId);
        }
    }
    
    // ===== GENERATE DAILY APPOINTMENT REPORT =====
    public void generateDailyReport() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              DAILY APPOINTMENT REPORT                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("Report Date: " + java.time.LocalDate.now());
        System.out.println("\n--- SUMMARY ---");
        System.out.println("Total Doctors: " + DataStore.doctors.size());
        System.out.println("Total Patients: " + DataStore.patients.size());
        
        int totalAppointments = 0;
        int totalWaiting = 0;
        for (Doctor doc : DataStore.doctors.values()) {
            totalAppointments += DataStore.getAppointmentCount(doc.getId());
            totalWaiting += DataStore.getWaitingListCount(doc.getId());
        }
        
        System.out.println("Total Confirmed Appointments: " + totalAppointments);
        System.out.println("Total Waiting List: " + totalWaiting);
        
        System.out.println("\n--- BY DOCTOR ---");
        for (Doctor doc : DataStore.doctors.values()) {
            int booked = DataStore.getAppointmentCount(doc.getId());
            int waiting = DataStore.getWaitingListCount(doc.getId());
            System.out.println(doc.getName() + " (" + doc.getSpecialization() + "): " + 
                             booked + " confirmed, " + waiting + " waiting");
        }
    }
    
    // ===== HOME VISIT SHORTEST PATH DEMO (Dijkstra's & Bellman-Ford) =====
    public void homeVisitShortestPathDemo() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         HOME VISIT SHORTEST PATH DEMONSTRATION               ║");
        System.out.println("║      (Dijkstra's Algorithm vs Bellman-Ford Algorithm)        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // Display location network
        System.out.println("\n📍 SERVICE AREA LOCATIONS:");
        System.out.println(DataStore.locationGraph.getLocations());
        
        System.out.println("\n🛣️  ROUTE NETWORK (distances in kilometers):");
        System.out.println("Hospital → Colombo: 5 km");
        System.out.println("Hospital → Negombo: 12 km");
        System.out.println("Hospital → Kandy: 115 km");
        System.out.println("Colombo → Negombo: 35 km");
        System.out.println("Colombo → Kandy: 115 km");
        System.out.println("Colombo → Galle: 119 km");
        System.out.println("Kandy → Jaffna: 235 km");
        System.out.println("Kandy → Matara: 175 km");
        System.out.println("Galle → Matara: 40 km");
        System.out.println("Negombo → Jaffna: 280 km");
        
        // Run Dijkstra's Algorithm
        System.out.println("\n" + "═".repeat(70));
        System.out.println("ALGORITHM 1: DIJKSTRA'S SHORTEST PATH");
        System.out.println("═".repeat(70));
        System.out.println("(Optimized for positive edge weights using priority queue)");
        System.out.println();
        
        HashMap<String, ShortestPathResult> dijkstraResults = DataStore.locationGraph.dijkstra("Hospital");
        
        System.out.println("\n📊 DIJKSTRA'S RESULTS - Shortest paths from Hospital:");
        System.out.println("─".repeat(70));
        for (String location : DataStore.locationGraph.getLocations()) {
            if (!location.equals("Hospital")) {
                ShortestPathResult result = dijkstraResults.get(location);
                if (result != null && result.getDistance() != Integer.MAX_VALUE) {
                    System.out.printf("%-15s | Distance: %4d km | Path: %s%n",
                        location,
                        result.getDistance(),
                        String.join(" → ", result.getPath()));
                } else {
                    System.out.printf("%-15s | No path available%n", location);
                }
            }
        }
        
        // Run Bellman-Ford Algorithm
        System.out.println("\n" + "═".repeat(70));
        System.out.println("ALGORITHM 2: BELLMAN-FORD SHORTEST PATH");
        System.out.println("═".repeat(70));
        System.out.println("(Can handle negative weights & detects negative cycles)");
        System.out.println();
        
        HashMap<String, ShortestPathResult> bellmanResults = DataStore.locationGraph.bellmanFord("Hospital");
        
        System.out.println("\n📊 BELLMAN-FORD RESULTS - Shortest paths from Hospital:");
        System.out.println("─".repeat(70));
        for (String location : DataStore.locationGraph.getLocations()) {
            if (!location.equals("Hospital")) {
                ShortestPathResult result = bellmanResults.get(location);
                if (result != null && result.getDistance() != Integer.MAX_VALUE) {
                    System.out.printf("%-15s | Distance: %4d km | Path: %s%n",
                        location,
                        result.getDistance(),
                        String.join(" → ", result.getPath()));
                } else {
                    System.out.printf("%-15s | No path available%n", location);
                }
            }
        }
        
        // Comparison
        System.out.println("\n" + "═".repeat(70));
        System.out.println("ALGORITHM COMPARISON");
        System.out.println("═".repeat(70));
        System.out.println("Both algorithms produce identical results for this positive-weight graph.");
        System.out.println("✓ Dijkstra's: Faster (O(E log V)) - Best for positive weights");
        System.out.println("✓ Bellman-Ford: More versatile (O(VE)) - Handles negative weights");
        
        // Display pending home visit requests
        System.out.println("\n" + "═".repeat(70));
        System.out.println("PENDING HOME VISIT REQUESTS");
        System.out.println("═".repeat(70));
        
        if (DataStore.homeVisitRequests.isEmpty()) {
            System.out.println("No pending home visit requests.");
        } else {
            int count = 1;
            for (HomeVisitRequest request : DataStore.homeVisitRequests.toList()) {
                System.out.println("\n" + count + ". " + request.getDisplayString());
                
                // Show optimal route
                ShortestPathResult route = dijkstraResults.get(request.getLocation());
                if (route != null) {
                    System.out.println("   Optimal Route: " + String.join(" → ", route.getPath()));
                    System.out.println("   Total Distance: " + route.getDistance() + " km");
                }
                count++;
            }
        }
    }
    
    // Dijkstra's Algorithm implementation (OLD - KEPT FOR REFERENCE)
    private void dijkstra(int[][] graph, int source, String[] cities) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[source] = 0;
        
        for (int count = 0; count < n - 1; count++) {
            int u = minDistance(dist, visited);
            visited[u] = true;
            
            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[u][v] != Integer.MAX_VALUE &&
                    dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        
        for (int i = 0; i < n; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.println(cities[source] + " → " + cities[i] + ": No path");
            } else {
                System.out.println(cities[source] + " → " + cities[i] + ": " + dist[i] + " km");
            }
        }
    }
    
    private int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        
        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }
    
    // ===== CREATE NEW SCHEDULE =====
    public void createNewSchedule(String doctorId, String slots, int slotLimit) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        // Update schedule text and per-doctor capacity
        doctor.setAvailableSlots(slots);
        DataStore.doctorSlotLimits.put(doctorId, slotLimit);
        // Ensure tracking maps are initialized
        DataStore.doctorCurrentAppointments.putIfAbsent(doctorId, 0);
        DataStore.doctorWaitingLists.putIfAbsent(doctorId, new WaitingQueue<>());

        DataStore.saveAll();
        System.out.println("\n✓ Schedule created successfully for Dr. " + doctor.getName());
        System.out.println("Available Slots: " + slots);
        System.out.println("Max Appointments Allowed: " + slotLimit);
    }
}





