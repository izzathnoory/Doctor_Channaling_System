public class Patient extends SystemUser {

    private String mobile;
    private String email;
    private String city;
    private int age;
    private String medicalHistory;
    private boolean isRegistered;

    public Patient(String id, String name, String mobile, String email,
                   String city, int age, String medicalHistory, String password) {

        super(id, name, password);
        this.mobile = mobile;
        this.email = email;
        this.city = city;
        this.age = age;
        this.medicalHistory = medicalHistory;
        this.isRegistered = true;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void displayProfile() {
        System.out.println("\n--- Patient Profile ---");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Mobile: " + mobile);
        System.out.println("Email: " + email);
        System.out.println("City: " + city);
        System.out.println("Medical History: " + medicalHistory);
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + age + ")";
    }
}
