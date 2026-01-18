public class Patient {

    private String id;
    private String name;
    private String mobile;
    private String email;
    private String city;

    private int age;
    
    private String medicalHistory;
    private String password;
    private boolean isRegistered;
    
    public Patient(String id, String name, String mobile, String email, String city, int age, String medicalHistory, String password) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.city = city;
        this.age = age;
        this.medicalHistory = medicalHistory;
        this.password = password;
        this.isRegistered = true;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAge(int age) {
        this.age = age;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
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

    public int getAge() {
        return age;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    // Display patient's detailed profile
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
