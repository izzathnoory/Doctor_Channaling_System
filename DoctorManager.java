import java.util.HashMap;

public class DoctorManager {

    private HashMap<String, Doctor> doctors;

    public DoctorManager() {
        // use centralized DB
        // ensure Db is loaded
        if (DataStore.doctors == null) {
            // initialize if needed
        }
    }

    // Register a doctor
    public void registerDoctor(String id, String name, String specialization, int consultationFee) {
        
        if (DataStore.doctors.containsKey(id)) {
            System.out.println("Doctor already exists!");
            return;
        }
        
        Doctor doctor = new Doctor(id, name, specialization, consultationFee);
        DataStore.doctors.put(id, doctor);
        NotificationService.sendNotification("Doctor registered: " + name);
    }

    // Search doctor by specialization
    public void searchDoctor(String specialization) {
        System.out.println("\n--- Available Doctors in " + specialization + " ---");
        boolean found = false;
        for (Doctor doc : DataStore.doctors.values()) {
            if (doc.getSpecialization().equalsIgnoreCase(specialization)) {
                System.out.println(doc);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No doctors found in this specialization.");
        }
    }

    // View full doctor details by ID
    public void viewDoctorDetails(String doctorId) {
        Doctor doctor = DataStore.doctors.get(doctorId);
        if (doctor != null) {
            doctor.displayFullDetails();
        } else {
            System.out.println("Doctor not found.");
        }
    }

    // Get all doctors with specific specialization
    public java.util.ArrayList<Doctor> getDoctorsBySpecialization(String specialization) {
        java.util.ArrayList<Doctor> result = new java.util.ArrayList<>();
        for (Doctor doc : DataStore.doctors.values()) {
            if (doc.getSpecialization().equalsIgnoreCase(specialization)) {
                result.add(doc);
            }
        }
        return result;
    }

    // Get doctor by ID
    public Doctor getDoctor(String id) {
        return DataStore.doctors.get(id);
    }
}

