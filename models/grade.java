package models;

public class grade {

    private int gradeId;
    private int enrollmentId;
    private String grade;
    private int gradePoint;

    public grade(int gradeId, int enrollmentId, String grade, int gradePoint) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.grade = grade;
        this.gradePoint = gradePoint;
    }

    public int getGradeId() { 
        return gradeId; }
    public int getEnrollmentId() {
         return enrollmentId; }
    public String getGrade() {
         return grade; }
    public int getGradePoint() { return gradePoint; }
}
