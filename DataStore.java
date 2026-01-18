import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    // ===== PATIENT AND DOCTOR DATA =====
    public static HashMap<String, Patient> patients = new HashMap<>();
    public static HashMap<String, Doctor> doctors = new HashMap<>();
    
    // ===== RECEPTIONIST CREDENTIALS =====
    public static HashMap<String, String> receptionistCredentials = new HashMap<>();

    // ===== APPOINTMENT DATA USING STACK AND QUEUE =====
    // Queue for confirmed appointments (FIFO - First In First Out)
    public static WaitingQueue<String> appointments = new WaitingQueue<>();
    
    // Stack for appointment history (LIFO - Last In First Out)
    public static Stack<String> appointmentHistory = new Stack<>(100);
    
    // Queue for cancelled appointments
    public static WaitingQueue<String> cancelledAppointments = new WaitingQueue<>();
    
    // ===== DOCTOR-SPECIFIC DATA =====
    // Waiting list queues for each doctor
    public static HashMap<String, WaitingQueue<String>> doctorWaitingLists = new HashMap<>();
    
    // Doctor appointment slot limits
    public static HashMap<String, Integer> doctorSlotLimits = new HashMap<>();
    public static HashMap<String, Integer> doctorCurrentAppointments = new HashMap<>();
    
    // Doctor schedules
    public static HashMap<String, String> doctorSchedules = new HashMap<>();
    
    // ===== HOME VISIT DATA =====
    // Queue for home visit requests
    public static WaitingQueue<HomeVisitRequest> homeVisitRequests = new WaitingQueue<>();
    
    // Location network graph for routing
    public static LocationRoudGraph locationGraph = new LocationRoudGraph();

    // Generate appointment number
    public static String generateAppointmentNumber() {
        return "APT-" + System.currentTimeMillis();
    }

    // Initialize all data
    public static void data() {
        // Sample patients with passwords
        patients.put("P001", new Patient("P001", "Ahmed", "0712345678", "ahmed@email.com", "Colombo", 28, "None", "A1234"));
        patients.put("P002", new Patient("P002", "Fatima", "0723456789", "fatima@email.com", "Colombo", 35, "Diabetes", "A1234"));
        patients.put("P003", new Patient("P003", "Hassan", "0734567890", "hassan@email.com", "Kandy", 42, "Hypertension", "A1234"));
        patients.put("P004", new Patient("P004", "Zainab", "0745678901", "zainab@email.com", "Galle", 30, "None", "A1234"));
        patients.put("P005", new Patient("P005", "Mohammed", "0756789012", "mohammed@email.com", "Jaffna", 55, "Heart Disease", "A1234"));

        // Sample doctors
        doctors.put("D001", new Doctor("D001", "Dr. Smith", "Cardiology", 2500));
        doctors.put("D002", new Doctor("D002", "Dr. Johnson", "Pediatrics", 2000));
        doctors.put("D003", new Doctor("D003", "Dr. Williams", "Orthopedics", 3000));
        doctors.put("D004", new Doctor("D004", "Dr. Brown", "Dermatology", 2200));
        doctors.put("D005", new Doctor("D005", "Dr. Davis", "Neurology", 3500));

        // Initialize doctor schedules and limits
        initializeDoctorSchedule("D001", "9-12PM", 2);
        initializeDoctorSchedule("D002", "9AM-5PM", 2);
        initializeDoctorSchedule("D003", "9AM-5PM", 2);
        initializeDoctorSchedule("D004", "9AM-5PM", 2);
        initializeDoctorSchedule("D005", "9AM-5PM", 2);

        // Pre-fill appointments for testing
        String now = java.time.Instant.now().toString();
        
        // D001 - FULL (2/2)
        bookConfirmedAppointment("P001-D001-05-Jan-2026 10:00 AM|" + generateAppointmentNumber() + "|" + now, "D001");
        bookConfirmedAppointment("P002-D001-05-Jan-2026 10:30 AM|" + generateAppointmentNumber() + "|" + now, "D001");
        
        // D002 - Partial (1/2)
        bookConfirmedAppointment("P003-D002-05-Jan-2026 11:00 AM|" + generateAppointmentNumber() + "|" + now, "D002");
        
        // D003 - Partial (1/2)
        bookConfirmedAppointment("P004-D003-05-Jan-2026 02:00 PM|" + generateAppointmentNumber() + "|" + now, "D003");

        // Receptionist credentials
        receptionistCredentials.put("admin", "admin123");
        receptionistCredentials.put("receptionist1", "reception123");
        receptionistCredentials.put("receptionist2", "reception123");
        
        // Initialize location graph for home visits
        initializeLocationGraph();
    }
    
    // Initialize location network for home visit routing
    private static void initializeLocationGraph() {
        // Add locations (cities/areas in Sri Lanka)
        locationGraph.addLocation("Hospital");
        locationGraph.addLocation("Colombo");
        locationGraph.addLocation("Kandy");
        locationGraph.addLocation("Galle");
        locationGraph.addLocation("Matara");
        locationGraph.addLocation("Negombo");
        locationGraph.addLocation("Jaffna");
        
        // Add routes with distances (in kilometers)
        // Hospital connections
        locationGraph.addRoute("Hospital", "Colombo", 5);
        locationGraph.addRoute("Hospital", "Negombo", 12);
        locationGraph.addRoute("Hospital", "Kandy", 115);
        
        // Colombo connections
        locationGraph.addRoute("Colombo", "Negombo", 35);
        locationGraph.addRoute("Colombo", "Kandy", 115);
        locationGraph.addRoute("Colombo", "Galle", 119);
        
        // Kandy connections
        locationGraph.addRoute("Kandy", "Jaffna", 235);
        locationGraph.addRoute("Kandy", "Matara", 175);
        
        // Galle connections
        locationGraph.addRoute("Galle", "Matara", 40);
        
        // Negombo connections
        locationGraph.addRoute("Negombo", "Jaffna", 280);
    }

    // Initialize doctor schedule, limits, and waiting queue
    private static void initializeDoctorSchedule(String doctorId, String schedule, int maxSlots) {
        Doctor doctor = doctors.get(doctorId);
        if (doctor != null) {
            doctor.setAvailableSlots(schedule);
        }
        doctorSchedules.put(doctorId, schedule);
        doctorSlotLimits.put(doctorId, maxSlots);
        doctorCurrentAppointments.put(doctorId, 0);
        doctorWaitingLists.put(doctorId, new WaitingQueue<>());
    }

    // Book confirmed appointment (internal)
    private static void bookConfirmedAppointment(String appointmentInfo, String doctorId) {
        appointments.enqueue(appointmentInfo);
        int current = doctorCurrentAppointments.getOrDefault(doctorId, 0);
        doctorCurrentAppointments.put(doctorId, current + 1);
    }

    // Get waiting list for doctor
    public static WaitingQueue<String> getWaitingList(String doctorId) {
        return doctorWaitingLists.getOrDefault(doctorId, new WaitingQueue<>());
    }

    // Get appointment count
    public static int getAppointmentCount(String doctorId) {
        return doctorCurrentAppointments.getOrDefault(doctorId, 0);
    }

    // Get waiting list count
    public static int getWaitingListCount(String doctorId) {
        WaitingQueue<String> waitingList = doctorWaitingLists.get(doctorId);
        return waitingList == null ? 0 : waitingList.size();
    }

    // Book appointment with waiting list logic
    public static boolean bookAppointmentWithWaitingList(String appointmentInfo, String doctorId) {
        if (!doctorSlotLimits.containsKey(doctorId)) {
            initializeDoctorSchedule(doctorId, "9AM-5PM", 5);
        }

        int currentSlots = doctorCurrentAppointments.getOrDefault(doctorId, 0);
        int maxSlots = doctorSlotLimits.getOrDefault(doctorId, 5);

        if (currentSlots < maxSlots) {
            appointments.enqueue(appointmentInfo);
            doctorCurrentAppointments.put(doctorId, currentSlots + 1);
            return true;
        } else {
            WaitingQueue<String> waitingList = doctorWaitingLists.get(doctorId);
            if (waitingList != null) {
                waitingList.enqueue(appointmentInfo);
            }
            return false;
        }
    }

    // Cancel and promote from waiting list
    public static String cancelAppointmentAndPromote(String doctorId, String appointmentInfo) {
        if (appointments.remove(appointmentInfo)) {
            int currentSlots = doctorCurrentAppointments.getOrDefault(doctorId, 0);
            doctorCurrentAppointments.put(doctorId, Math.max(0, currentSlots - 1));

            WaitingQueue<String> waitingList = doctorWaitingLists.get(doctorId);
            if (waitingList != null && !waitingList.isEmpty()) {
                String waitingPatient = waitingList.dequeue();
                appointments.enqueue(waitingPatient);
                doctorCurrentAppointments.put(doctorId, 
                    doctorCurrentAppointments.getOrDefault(doctorId, 0) + 1);
                return appointmentInfo + " | PROMOTED FROM WAITING: " + waitingPatient;
            }
        }
        return appointmentInfo;
    }

    // Initialize doctor slots
    public static void initializeDoctorSlots(int slotsPerDoctor) {
        for (String doctorId : doctors.keySet()) {
            doctorSlotLimits.put(doctorId, slotsPerDoctor);
            doctorCurrentAppointments.put(doctorId, 0);
            doctorWaitingLists.putIfAbsent(doctorId, new WaitingQueue<>());
        }
    }

    // Add cancelled appointment
    public static void addCancelledAppointment(String appointmentInfo) {
        String timestamp = java.time.Instant.now().toString();
        String record = timestamp + "|" + appointmentInfo;
        cancelledAppointments.enqueue(record);
    }

    // Get all cancelled appointments
    public static List<String> getCancelledAppointments() {
        return cancelledAppointments.toList();
    }

    // Get doctor schedule
    public static String getDoctorSchedule(String doctorId) {
        return doctorSchedules.getOrDefault(doctorId, "Not Available");
    }

    // Get comprehensive appointment statistics
    public static String getAppointmentStats() {
        int totalConfirmed = appointments.size();
        int totalHistory = appointmentHistory.size();
        int totalCancelled = cancelledAppointments.size();
        int totalWaiting = 0;
        for (WaitingQueue<String> queue : doctorWaitingLists.values()) {
            totalWaiting += queue.size();
        }
        
        return String.format("Confirmed: %d | History: %d | Cancelled: %d | Waiting: %d",
            totalConfirmed, totalHistory, totalCancelled, totalWaiting);
    }

    // Compatibility methods
    public static void saveAll() {
        // No-op for in-memory version
    }

    public static void initHistorySize(int size) {
        // Already set in Stack constructor
    }

    private DataStore() {
        // Utility class
    }
}
