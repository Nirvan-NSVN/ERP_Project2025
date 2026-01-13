package models;

public class instructor {

    private int instructorId;
    private int userId;
    private String name;
    private String department;

    public instructor(int instructorId, int userId, String name, String department) {
        this.instructorId = instructorId;
        this.userId = userId;
        this.name = name;
        this.department = department;
    }

    public int getInstructorId() 
    { 
        return instructorId; }
    public int getUserId() { 
        return userId; }
    public String getName() { 
        return name; }
    public String getDepartment() { 
        return department; }
    public void setName(String name) {
    this.name = name;
}

public void setDepartment(String department) {
    this.department = department;
}
}
