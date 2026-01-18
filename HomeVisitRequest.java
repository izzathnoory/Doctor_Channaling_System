public class HomeVisitRequest {
    private String requestId;
    private String patientId;
    private String patientName;
    private String location;
    private String doctorId;
    private String requestedDate;
    private String requestedTime;
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private String requestTime;
    
    public HomeVisitRequest(String requestId, String patientId, String patientName, String location, 
                           String doctorId, String requestedDate, String requestedTime) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.location = location;
        this.doctorId = doctorId;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.status = "PENDING";
        this.requestTime = java.time.Instant.now().toString();
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getLocation() { return location; }
    public String getDoctorId() { return doctorId; }
    public String getRequestedDate() { return requestedDate; }
    public String getRequestedTime() { return requestedTime; }
    public String getStatus() { return status; }
    public String getRequestTime() { return requestTime; }
    
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return requestId + "|" + patientId + "|" + patientName + "|" + location + "|" + 
               doctorId + "|" + requestedDate + "|" + requestedTime + "|" + status + "|" + requestTime;
    }
    
    public String getDisplayString() {
        return String.format("Request ID: %s | Patient: %s (%s)\nLocation: %s | Doctor: %s\nDate: %s %s | Status: %s",
            requestId, patientName, patientId, location, doctorId, requestedDate, requestedTime, status);
    }
}
