package models;

public class enrollment {

    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status;   // pending, active, dropped

    public enrollment(int enrollmentId, int studentId, int sectionId, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }
    public int getStudentId() {
        return studentId;
    }

    public int getSectionId() {
        return sectionId;
    }
    public String getStatus() {
        return status;
    }
}
