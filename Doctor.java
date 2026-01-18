public class Doctor {

    private String id;
    private String name;
    private String specialization;
    private int consultationFee;
    private String availableSlots; // e.g., "9AM-12PM, 2PM-5PM"

    public Doctor(String id, String name, String specialization, int consultationFee) {
        this(id, name, specialization, consultationFee, "9AM-5PM");
    }

    public Doctor(String id, String name, String specialization, int consultationFee, String availableSlots) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.availableSlots = availableSlots;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(int consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(String availableSlots) {
        this.availableSlots = availableSlots;
    }

    // Display complete doctor details including availability
    public void displayFullDetails() {
        System.out.println("\n--- Doctor Details ---");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Specialization: " + specialization);
        System.out.println("Consultation Fee: Rs. " + consultationFee);
        System.out.println("Available Slots: " + availableSlots);
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + specialization + ") Fee: Rs." + consultationFee + " | Available: " + availableSlots;
    }
}
