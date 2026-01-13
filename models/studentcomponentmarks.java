package models;

public class studentcomponentmarks {
    private int enrollmentId;
    private int componentId;
    private double marksObtained;
    private double maxMarks;
    private int gradedBy;
    private String gradeTime;

    public studentcomponentmarks(int enrollmentId, int componentId, double marksObtained,
                                 double maxMarks, int gradedBy, String gradeTime) {

        this.enrollmentId = enrollmentId;
        this.componentId = componentId;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.gradedBy = gradedBy;
        this.gradeTime = gradeTime;
    }

    public int getEnrollmentId() { 
        return enrollmentId; }
    public int getComponentId() { return componentId; }
    public double getMarksObtained() { 
        return marksObtained; }
    public double getMaxMarks() { return maxMarks; }
    public int getGradedBy() {
         return gradedBy; }
    public String getGradeTime() { return gradeTime; }
}
