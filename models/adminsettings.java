package models;

public class adminsettings {

    private int id;
    private boolean registrationLocked;
    private boolean maintenanceMode;
    private String announcement;
    private String instructorAnnouncement;

    public adminsettings(int id, boolean registrationLocked,
                         boolean maintenanceMode, String announcement,
                         String instructorAnnouncement) {

        this.id = id;
        this.registrationLocked = registrationLocked;
        this.maintenanceMode = maintenanceMode;
        this.announcement = announcement;
        this.instructorAnnouncement = instructorAnnouncement;
    }

    public int getId() { return id; }
    public boolean isRegistrationLocked() { return registrationLocked; }
    public boolean isMaintenanceMode() { return maintenanceMode; }
    public String getAnnouncement() { return announcement; }
    public String getInstructorAnnouncement() { return instructorAnnouncement; }

    public void setRegistrationLocked(boolean v) { this.registrationLocked = v; }
    public void setMaintenanceMode(boolean v) { this.maintenanceMode = v; }
    public void setAnnouncement(String v) { this.announcement = v; }
    public void setInstructorAnnouncement(String v) { this.instructorAnnouncement = v; }
}
