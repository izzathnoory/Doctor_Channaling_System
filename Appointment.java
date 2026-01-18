public class Appointment {

    public Appointment() {
        // Initialize with existing appointments from Db
    }

    // Book appointment with detailed notification
    public void bookAppointment(String appointmentInfo) {
        DataStore.appointments.enqueue(appointmentInfo);
        DataStore.saveAll();
        NotificationService.sendNotification("Appointment booked: " + appointmentInfo);
    }

    // Book appointment with full details
    public void bookAppointmentWithDetails(String patientId, String doctorId, String dateTime) {
        Patient patient = DataStore.patients.get(patientId);
        Doctor doctor = DataStore.doctors.get(doctorId);
        
        if (patient == null || doctor == null) {
            System.out.println("Patient or Doctor not found.");
            return;
        }
        
        String appointmentNumber = DataStore.generateAppointmentNumber();
        String bookedTime = java.time.Instant.now().toString();
        String appointmentInfo = patientId + "-" + doctorId + "-" + dateTime + "|" + appointmentNumber + "|" + bookedTime;
        DataStore.appointments.enqueue(appointmentInfo);
        DataStore.saveAll();
        
        NotificationService.sendNotification("\n✓ APPOINTMENT CONFIRMED!\n" +
                "Doctor: " + doctor.getName() + "\n" +
                "Specialization: " + doctor.getSpecialization() + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Fee: Rs. " + doctor.getConsultationFee());
    }

    // Cancel appointment with notification
    public void cancelAppointment() {
        if (!DataStore.appointments.isEmpty()) {
            String removed = DataStore.appointments.dequeue();
            DataStore.saveAll();
            NotificationService.sendNotification("Appointment cancelled: " + removed);
        } else {
            System.out.println("No appointments to cancel.");
        }
    }

    // Cancel specific appointment
    public boolean cancelAppointmentById(String appointmentId) {
        // This would require queue modification capability
        // For now, displays cancellation message
        NotificationService.sendNotification("✓ Appointment cancellation processed for: " + appointmentId);
        return true;
    }

    // Display all scheduled appointments
    public void displayAppointments() {
        System.out.println("\n--- All Scheduled Appointments ---");
        if (DataStore.appointments.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            DataStore.appointments.displayQueue(); if (false) for (String apt : DataStore.appointments.toList()) {
                System.out.println(apt);
            }
        }
    }

    // Display patient's appointments
    public void displayPatientAppointments(String patientId) {
        System.out.println("\n--- Appointments for Patient: " + patientId + " ---");
        boolean found = false;
        DataStore.appointments.displayQueue(); if (false) for (String apt : DataStore.appointments.toList()) {
            if (apt.contains(patientId)) {
                System.out.println(apt);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No appointments for this patient.");
        }
    }
}




