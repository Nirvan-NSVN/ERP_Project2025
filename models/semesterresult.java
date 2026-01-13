package models;

public class semesterresult {

    private int resultId;
    private int studentId;
    private int semester;
    private double sgpa;

    public semesterresult(int resultId, int studentId, int semester, double sgpa) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.semester = semester;
        this.sgpa = sgpa;
    }

    public int getResultId() { return resultId; }
    public int getStudentId() { return studentId; }
    public int getSemester() { return semester; }
    public double getSgpa() { return sgpa; }
}