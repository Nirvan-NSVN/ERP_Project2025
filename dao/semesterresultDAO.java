package dao;

import database.ERPDB;
import models.semesterresult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class semesterresultDAO {
    public semesterresult getSemesterSGPA(int studentId, int semester){
        String sql = "SELECT result_id, student_id, semester, sgpa " +
                     "FROM semester_result WHERE student_id = ? AND semester = ?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2, semester);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSemesterResult(rs);

        } catch(Exception e) {e.printStackTrace();}

        return null;
    }

    public List<Integer> getSemesters(int studentId){
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM semester_result " +
                     "WHERE student_id = ? ORDER BY semester";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getInt("semester"));

        }catch(Exception e){e.printStackTrace();}
        return list;
    }

    public List<Object[]> getSemesterResult(int studentId, int semester) {

        List<Object[]> list=new ArrayList<>();

        String sql =
                "SELECT c.code, c.title, c.credits, g.grade, g.grade_point " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND s.semester = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2, semester);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new Object[]{
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("grade"),
                        rs.getInt("grade_point")
                });
            }

        }catch(Exception e){ e.printStackTrace(); }

        return list;
    }

    public double getCGPA(int studentId){

    String sql =
        "SELECT COALESCE(SUM(co.credits * g.grade_point),0) AS total_points, " +
        "       COALESCE(SUM(co.credits),0) AS total_credits " +
        "FROM enrollments e " +
        "JOIN sections s ON e.section_id = s.section_id " +
        "JOIN courses co ON s.course_id = co.course_id " +
        "JOIN grades g ON e.enrollment_id = g.enrollment_id " +
        "WHERE e.student_id = ?";

    try(Connection c =ERPDB.getERPConnection();
         PreparedStatement ps =c.prepareStatement(sql)){

        ps.setInt(1, studentId);
        ResultSet rs =ps.executeQuery();

        if (rs.next()){
            double totalPoints = rs.getDouble("total_points");
            double totalCredits = rs.getDouble("total_credits");

            if (totalCredits == 0) return 0.0;
            return totalPoints / totalCredits; // correct CGPA
        }

    }catch(Exception e) {
        e.printStackTrace();
    }
    return 0.0;
}

    public List<semesterresult> getAllSemesters(int studentId){
        List<semesterresult> list = new ArrayList<>();

        String sql =
            "SELECT result_id, student_id, semester, sgpa " +
            "FROM semester_result WHERE student_id = ? ORDER BY semester";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapSemesterResult(rs));

        }catch(Exception e) {e.printStackTrace();}
        return list;
    }


    public boolean insertSGPA(int studentId, int semester, double sgpa){

        String sql ="INSERT INTO semester_result(student_id, semester, sgpa) VALUES(?, ?, ?)";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,studentId);
            ps.setInt(2,semester);
            ps.setDouble(3, sgpa);
            return ps.executeUpdate() >0;

        }catch(SQLIntegrityConstraintViolationException dup){
            return false;
        }catch(Exception e){e.printStackTrace();}

        return false;
    }

    public boolean updateSGPA(int studentId, int semester, double sgpa){

        String sql ="UPDATE semester_result SET sgpa = ? WHERE student_id = ? AND semester = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setDouble(1, sgpa);
            ps.setInt(2, studentId);
            ps.setInt(3, semester);

            return ps.executeUpdate() > 0;

        }catch(Exception e){e.printStackTrace();}
        return false;
    }
 
    public boolean deleteSemesterResult(int studentId, int semester){

        String sql ="DELETE FROM semester_result WHERE student_id = ? AND semester = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            return ps.executeUpdate() > 0;

        }catch(Exception e) {e.printStackTrace();}

        return false;
    }


    private semesterresult mapSemesterResult(ResultSet rs) throws Exception {
        return new semesterresult(
                rs.getInt("result_id"),
                rs.getInt("student_id"),
                rs.getInt("semester"),
                rs.getDouble("sgpa")
        );
    }

public void updateAfterMarks(int enrollmentId) {

    try(var con =ERPDB.getERPConnection()){
        int studentId = -1;
        int semester = -1;

        var ps0 = con.prepareStatement("""
            SELECT e.student_id, s.semester
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            WHERE e.enrollment_id = ?
        """);

        ps0.setInt(1, enrollmentId);
        var rs0 = ps0.executeQuery();
        if (!rs0.next()) return;

        studentId = rs0.getInt(1);
        semester  = rs0.getInt(2);

        // Compute weighted score using weightage% (weightage / 100)
       
        double score = 0;
        var psScore = con.prepareStatement("""
            SELECT COALESCE(
                SUM(
                   (m.marks_obtained / NULLIF(cc.max_marks,0)) 
                   * (cc.weightage / 100)
                ),
                0
            ) AS total
            FROM student_component_marks m
            JOIN course_components cc ON m.component_id = cc.component_id
            WHERE m.enrollment_id = ?
        """);

        psScore.setInt(1, enrollmentId);
        var rsScore = psScore.executeQuery();
        if (rsScore.next()) score = rsScore.getDouble("total");

        // Convert score to grade
        String grade = computeGrade(score * 100);  // score is 0–1, convert to 0–100
        int gp = gradeToPoint(grade);

        //Insert/update grades table
        var ps2 = con.prepareStatement("""
            INSERT INTO grades(enrollment_id, grade, grade_point)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                grade = VALUES(grade),
                grade_point = VALUES(grade_point)
        """);

        ps2.setInt(1, enrollmentId);
        ps2.setString(2, grade);
        ps2.setInt(3, gp);
        ps2.executeUpdate();

    
        double num = 0;
        double den = 0;

        var psSGPA = con.prepareStatement("""
            SELECT 
                c.credits,
                g.grade_point
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            JOIN courses c ON s.course_id = c.course_id
            LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id
            WHERE e.student_id = ? AND s.semester = ?
        """);
        psSGPA.setInt(1, studentId);
        psSGPA.setInt(2, semester);
        var rsSGPA = psSGPA.executeQuery();

        while(rsSGPA.next()){
            int credits = rsSGPA.getInt(1);
            int gradePoint = rsSGPA.getInt(2);
            num += credits * gradePoint;
            den += credits;
        }

        double sgpa = (den == 0) ? 0 : num / den;
        // Insert/update SGPA in semester_result
        var psSR = con.prepareStatement("""
            INSERT INTO semester_result(student_id, semester, sgpa)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE sgpa = VALUES(sgpa)
        """);
        psSR.setInt(1, studentId);
        psSR.setInt(2, semester);
        psSR.setDouble(3, sgpa);
        psSR.executeUpdate();

        //  SGPA stored only in semester_result table

    }catch(Exception ex){
        ex.printStackTrace();
    }
}



private int gradeToPoint(String grade) {
    return switch (grade){
        case "A+" -> 10;
        case "A"  -> 9;
        case "B+" -> 8;
        case "B"  -> 7;
        case "C"  -> 6;
        case "D"  -> 5;
        default -> 0;
    };
}
private String computeGrade(double percent){
    if (percent >= 90) return "A+";
    if (percent >= 80) return "A";
    if (percent >= 70) return "B+";
    if (percent >= 60) return "B";
    if (percent >= 50) return "C";
    if (percent >= 40) return "D";
    return "F";
}


public boolean hasGrade(int enrollmentId){
    String sql = "SELECT 1 FROM grades WHERE enrollment_id = ? LIMIT 1";

    try(Connection c = ERPDB.getERPConnection();
         PreparedStatement ps = c.prepareStatement(sql)){

        ps.setInt(1, enrollmentId);
        return ps.executeQuery().next();   // true if grade exists

    }catch(Exception e){
        e.printStackTrace();
    }
    return false;
}
public List<Object[]> getTranscriptData(int studentId){
    List<Object[]> list = new ArrayList<>();

    String sql = """
        SELECT 
            c.code,
            c.title,
            c.credits,
            g.grade,
            g.grade_point,
            s.semester
        FROM grades g
        JOIN enrollments e ON g.enrollment_id = e.enrollment_id
        JOIN sections s ON e.section_id = s.section_id
        JOIN courses c ON s.course_id = c.course_id
        WHERE e.student_id = ?
        ORDER BY s.semester, c.code;
    """;

    try(Connection con =ERPDB.getERPConnection();
         PreparedStatement ps =con.prepareStatement(sql)){

        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Object[]{
                    rs.getString("code"),
                    rs.getString("title"),
                    rs.getInt("credits"),
                    rs.getString("grade"),
                    rs.getInt("grade_point"),
                    rs.getInt("semester")
            });
        }

    }catch(Exception e){
        e.printStackTrace();
    }
    return list;
}




}
