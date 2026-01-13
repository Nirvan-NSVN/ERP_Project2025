package models;

public class courseoffering {

    private int offeringId;
    private int courseId;
    private int semester;
    private int year;
    private String offeredToProgram;  // SET(BTECH,MTECH,PHD)
    private int createdBy;

    public courseoffering(int offeringId, int courseId, int semester, int year,
                          String offeredToProgram, int createdBy) {

        this.offeringId = offeringId;
        this.courseId = courseId;
        this.semester = semester;
        this.year = year;
        this.offeredToProgram = offeredToProgram;
        this.createdBy = createdBy;
    }

    public int getOfferingId() { return offeringId; }
    public int getCourseId() { return courseId; }
    public int getSemester() { return semester; }
    public int getYear() { return year; }
    public String getOfferedToProgram() { return offeredToProgram; }
    public int getCreatedBy() { return createdBy; }
}
