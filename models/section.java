package models;

public class section {

    private int sectionId;
    private int courseId;
    private int instructorId;
    private String dayTime;
    private String room;
    private int capacity;
    private int semester;
    private int year;
    private boolean gradeLocked;

    public section(int sectionId, int courseId, int instructorId,
                   String dayTime, String room, int capacity,
                   int semester, int year, boolean gradeLocked) {

        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.gradeLocked = gradeLocked;
    }

    public int getSectionId() {
         return sectionId; }
    public int getCourseId() { 
        return courseId; }
    public int getInstructorId() {
         return instructorId; }
    public String getDayTime() { 
        return dayTime; }
    public String getRoom() { 
        return room; }
    public int getCapacity() { 
        return capacity; }
    public int getSemester() {
         return semester; }
    public int getYear() {
         return year; }
    public boolean isGradeLocked() { 
        return gradeLocked; }
}
