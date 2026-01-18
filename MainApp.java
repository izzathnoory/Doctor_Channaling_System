import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        PatientManager patientManager = new PatientManager(50); // stack size 50
        DoctorManager doctorManager = new DoctorManager();
        Appointment appointmentManager = new Appointment();
        ReceptionistManager receptionistManager = new ReceptionistManager();
        LoginManager loginManager = new LoginManager();

        // Load sample data
        DataStore.data();

        boolean exit = false;
        boolean patientLogged = false;
        boolean receptionistLogged = false;
        String currentPatientId = null;

        while (!exit) {

            // ===================== MAIN MENU =====================
            if (!patientLogged && !receptionistLogged) {

                System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
                System.out.println("║          HOSPITAL APPOINTMENT MANAGEMENT SYSTEM              ║");
                System.out.println("╚══════════════════════════════════════════════════════════════╝");
                System.out.println("\n1. Login");
                System.out.println("2. Register Patient");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("\n1. Patient Login\n2. Receptionist Login\n3. Cancel");
                        System.out.print("Choose login type: ");
                        int lt = sc.nextInt();
                        sc.nextLine();

                        if (lt == 1) {
                            String id = loginManager.patientLogin(patientManager, sc);
                            if (id != null) {
                                patientLogged = true;
                                currentPatientId = id;
                            }
                        } else if (lt == 2) {
                            if (loginManager.receptionistLogin(sc)) {
                                receptionistLogged = true;
                            }
                        }
                        break;

                    case 2:
                        System.out.print("Enter Patient ID: ");
                        String pid = sc.nextLine();
                        System.out.print("Enter Name: ");
                        String pname = sc.nextLine();
                        System.out.print("Enter Mobile: ");
                        String pmobile = sc.nextLine();
                        System.out.print("Enter Email: ");
                        String pemail = sc.nextLine();
                        System.out.print("Enter City: ");
                        String pcity = sc.nextLine();
                        System.out.print("Enter Age: ");
                        int page = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Medical History: ");
                        String pmed = sc.nextLine();
                        System.out.print("Set a password: ");
                        String ppass = sc.nextLine();

                        patientManager.registerPatient(pid, pname, pmobile, pemail, pcity, page, pmed, ppass);
                        break;

                    case 3:
                        exit = true;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

            // ===================== PATIENT PORTAL =====================
            else if (patientLogged) {

                System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
                System.out.println("║                  PATIENT PORTAL                              ║");
                System.out.println("╚══════════════════════════════════════════════════════════════╝");
                System.out.println("1. Search Doctor by Specialization");
                System.out.println("2. View All Available Schedules");
                System.out.println("3. Book New Appointment");
                System.out.println("4. My Booking History");
                System.out.println("5. Cancel Appointment");
                System.out.println("6. Request Reschedule");
                System.out.println("7. Request Home Visit");
                System.out.println("8. View My Home Visit Requests");
                System.out.println("9. Logout");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter Specialization: ");
                        patientManager.displayDoctorListBySpecialization(sc.nextLine());
                        break;

                    case 2:
                        System.out.print("Enter preferred time or press Enter for all: ");
                        String timeSlot = sc.nextLine();
                        if (timeSlot.trim().isEmpty())
                            patientManager.viewAllAvailableSchedules();
                        else
                            patientManager.viewDoctorsByTimeSlot(timeSlot);
                        break;

                    case 3:
                        System.out.print("Enter Doctor ID: ");
                        String doctorId = sc.nextLine();
                        System.out.print("Enter Appointment Date & Time: ");
                        String adt = sc.nextLine();
                        patientManager.bookAppointment(currentPatientId, doctorId, adt);
                        break;

                    case 4:
                        patientManager.viewMyAppointmentHistory(currentPatientId);
                        break;

                    case 5:
                        patientManager.cancelAppointment(currentPatientId);
                        break;

                    case 6:
                        System.out.print("Enter old appointment info: ");
                        String oldApp = sc.nextLine();
                        System.out.print("Enter new date & time: ");
                        String newApp = sc.nextLine();
                        patientManager.requestReschedule(currentPatientId, oldApp, newApp);
                        break;

                    case 7:
                        System.out.println("Available locations: " + DataStore.locationGraph.getLocations());
                        System.out.print("Enter your location: ");
                        String loc = sc.nextLine();
                        System.out.print("Enter Doctor ID: ");
                        String hvDoc = sc.nextLine();
                        System.out.print("Enter date: ");
                        String hvDate = sc.nextLine();
                        System.out.print("Enter time: ");
                        String hvTime = sc.nextLine();
                        patientManager.requestHomeVisit(currentPatientId, loc, hvDoc, hvDate, hvTime);
                        break;

                    case 8:
                        patientManager.viewHomeVisitRequests(currentPatientId);
                        break;

                    case 9:
                        patientLogged = false;
                        currentPatientId = null;
                        System.out.println("✓ Logged out successfully.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

            // ===================== ADMIN / RECEPTIONIST PORTAL =====================
            else if (receptionistLogged) {

                System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
                System.out.println("║                    ADMIN PORTAL                              ║");
                System.out.println("╚══════════════════════════════════════════════════════════════╝");
                System.out.println("1. View All Patients");
                System.out.println("2. Sort Patients by Age");
                System.out.println("3. View All Schedules");
                System.out.println("4. View Confirm Appointments");
                System.out.println("5. View All Pending Queue");
                System.out.println("6. View Full Booking History");
                System.out.println("7. Cancel Appointment by ID");
                System.out.println("8. Daily Appointment Report");
                System.out.println("9. Home Visit Shortest Path Demo");
                System.out.println("10. Register New Patient");
                System.out.println("11. Register New Doctor");
                System.out.println("12. Create New Schedule");
                System.out.println("13. Logout");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        receptionistManager.listAllPatients();
                        break;

                    case 2:
                        System.out.println("\nChoose Sorting Algorithm:");
                        System.out.println("1. Bubble Sort");
                        System.out.println("2. Insertion Sort");
                        System.out.print("Enter choice: ");
                        int sortChoice = sc.nextInt();
                        sc.nextLine();

                        if (sortChoice == 1) {
                            patientManager.bubbleSortPatientsByAge();
                            System.out.println("✓ Sorted using Bubble Sort");
                        } else if (sortChoice == 2) {
                            patientManager.insertionSortPatientsByAge();
                            System.out.println("✓ Sorted using Insertion Sort");
                        } else {
                            System.out.println("Invalid sorting option.");
                        }
                        patientManager.displayPatients();
                        break;

                    case 3:
                        receptionistManager.viewAllSchedules();
                        break;

                    case 4:
                        receptionistManager.viewConfirmAppointments();
                        break;

                    case 5:
                        receptionistManager.viewAllWaitingLists();
                        break;

                    case 6:
                        receptionistManager.viewFullBookingHistory();
                        break;

                    case 7:
                        System.out.print("Enter Appointment ID: ");
                        receptionistManager.cancelAppointmentById(sc.nextLine());
                        break;

                    case 8:
                        receptionistManager.generateDailyReport();
                        break;

                    case 9:
                        receptionistManager.homeVisitShortestPathDemo();
                        break;

                    case 10:
                        System.out.print("Enter Patient ID: ");
                        String pid = sc.nextLine();
                        System.out.print("Enter Name: ");
                        String pname = sc.nextLine();
                        System.out.print("Enter Mobile: ");
                        String pmobile = sc.nextLine();
                        System.out.print("Enter Email: ");
                        String pemail = sc.nextLine();
                        System.out.print("Enter City: ");
                        String pcity = sc.nextLine();
                        System.out.print("Enter Age: ");
                        int age = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Medical History: ");
                        String pmed = sc.nextLine();
                        System.out.print("Enter Password: ");
                        String ppass = sc.nextLine();
                        receptionistManager.registerPatient(pid, pname, pmobile, pemail, pcity, age, pmed, ppass);
                        break;

                    case 11:
                        System.out.print("Enter Doctor ID: ");
                        String did = sc.nextLine();
                        System.out.print("Enter Name: ");
                        String dname = sc.nextLine();
                        System.out.print("Enter Specialization: ");
                        String spec = sc.nextLine();
                        System.out.print("Enter Fee: ");
                        int fee = sc.nextInt();
                        sc.nextLine();
                        receptionistManager.registerDoctor(did, dname, spec, fee);
                        break;

                    case 12:
                        System.out.print("Enter Doctor ID: ");
                        String docId = sc.nextLine();
                        System.out.print("Enter Slots: ");
                        String slots = sc.nextLine();
                        System.out.print("Enter Max Appointments: ");
                        int limit = sc.nextInt();
                        sc.nextLine();
                        receptionistManager.createNewSchedule(docId, slots, limit);
                        break;

                    case 13:
                        receptionistLogged = false;
                        System.out.println("✓ Logged out successfully.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }

        sc.close();
        DataStore.saveAll();
        System.out.println("\n✓ System Closed. Data saved.");
    }
}
