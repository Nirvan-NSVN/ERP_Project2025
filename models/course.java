package models;

public class course {

    private int courseId;
    private String code;
    private String title;
    private int credits;
    private String department;
    private String allowedPrograms;  // SET('BTECH','MTECH','PHD')
    private int minSemester;
    private double minCgpa;

    public course(int courseId, String code, String title,
                  int credits, String department,
                  String allowedPrograms, int minSemester, double minCgpa) {

        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.department = department;
        this.allowedPrograms = allowedPrograms;
        this.minSemester = minSemester;
        this.minCgpa = minCgpa;
    }

    public int getCourseId() { return courseId; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public String getAllowedPrograms() { return allowedPrograms; }
    public int getMinSemester() { return minSemester; }

    // original getter
    public double getMinCgpa() { return minCgpa; }

    public double getMinCGPA() { return minCgpa; }

    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setDepartment(String department) { this.department = department; }
    public void setAllowedPrograms(String allowedPrograms) { this.allowedPrograms = allowedPrograms; }
    public void setMinSemester(int minSemester) { this.minSemester = minSemester; }

    public void setMinCGPA(double minCgpa) { this.minCgpa = minCgpa; }
}
