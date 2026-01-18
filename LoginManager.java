// import java.util.HashMap;
import java.util.Scanner;

public class LoginManager {

    public LoginManager() {
        // Credentials now managed in-memory via Db class
    }

    // Verify receptionist login credentials from Db
    public boolean verifyLogin(String username, String password) {
        String storedPassword = DataStore.receptionistCredentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    // Add new receptionist user (in-memory only)
    public void addReceptionist(String username, String password) {
        DataStore.receptionistCredentials.put(username, password);
    }

    // Update receptionist password (in-memory only)
    public boolean updateReceptionistPassword(String username, String oldPassword, String newPassword) {
        String stored = DataStore.receptionistCredentials.get(username);
        if (stored != null && stored.equals(oldPassword)) {
            DataStore.receptionistCredentials.put(username, newPassword);
            return true;
        }
        return false;
    }

    public String patientLogin(PatientManager pm, Scanner sc) {
        System.out.print("Enter Patient ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine();
        Patient p = pm.getPatient(id);
        if (p == null) {
            System.out.println("No such patient. Please register first.");
            return null;
        }
        if (p.getPassword() != null && p.getPassword().equals(pw)) {
            System.out.println("Patient login successful.");
            return id;
        } else {
            System.out.println("Invalid password.");
            return null;
        }
    }

    public boolean receptionistLogin(Scanner sc) {
        System.out.print("Enter Receptionist username: ");
        String user = sc.nextLine();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine();
        String stored = DataStore.receptionistCredentials.get(user);
        if (stored != null && stored.equals(pw)) {
            System.out.println("Receptionist login successful.");
            return true;
        }
        System.out.println("Invalid receptionist credentials.");
        return false;
    }
}

