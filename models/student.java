package models;

public class student {

    private int studentId;
    private int userId;
    private String rollNo;
    private String name;
    private String programType;   // BTech,mtech,phd
    private int semester;
    private String hostel;

    public student(int studentId, int userId, String rollNo, String name,
                   String programType, int semester, String hostel) {

        this.studentId = studentId;
        this.userId = userId;
        this.rollNo = rollNo;
        this.name = name;
        this.programType = programType;
        this.semester = semester;
        this.hostel = hostel;
    }

    public int getStudentId() {
         return studentId; }
    public int getUserId() { return userId; }
    public String getRollNo() { return rollNo; }
    public String getName() { 
        return name; }
    public String getProgramType() { return programType; }
    public int getSemester() { 
        return semester; }
    public String getHostel() { return hostel; }

    public void setSemester(int semester) { this.semester = semester; }
    public void setHostel(String hostel) { this.hostel = hostel; }
    public void setRollNo(String rollNo) {
    this.rollNo = rollNo;
}

public void setName(String name) {
    this.name = name;
}

public void setProgramType(String programType) {
    this.programType = programType;
}

}

