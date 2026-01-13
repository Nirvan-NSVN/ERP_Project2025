package dao;

import database.ERPDB;
import models.course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class courseDAO {
    public course getCourse(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, courseId);
            ResultSet rs =ps.executeQuery();

            if (rs.next()){
                return mapCourse(rs);
            }

        } catch(Exception e){e.printStackTrace();}
        return null;
    }
    public course getCourseByCode(String code){
        String sql ="SELECT * FROM courses WHERE code = ?";

        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setString(1, code);
            ResultSet rs =ps.executeQuery();

            if (rs.next())return mapCourse(rs);
        } catch(Exception e){e.printStackTrace();}
        return null;
    }
    public List<course> getAllCourses(){
        List<course> list = new ArrayList<>();
        String sql ="SELECT * FROM courses ORDER BY code";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ResultSet rs =ps.executeQuery();

            while(rs.next()) list.add(mapCourse(rs));

        }catch(Exception e){e.printStackTrace();}
        return list;
    }
    public List<course> getEligibleCourses(String programType, int currentSem, double cgpa) {

    List<course> list =new ArrayList<>();
    String sql =
        "SELECT * FROM courses " +
        "WHERE FIND_IN_SET(?, REPLACE(allowed_programs, ' ', '')) " +
        "AND min_semester <= ? " +
        "AND min_cgpa <= ?";

    try(Connection c =ERPDB.getERPConnection();
         PreparedStatement ps =c.prepareStatement(sql)){

        ps.setString(1,programType.trim());
        ps.setInt(2,currentSem);
        ps.setDouble(3,cgpa);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            list.add(mapCourse(rs));
        }

    }catch(Exception e){
        e.printStackTrace();
    }
    return list;
}

    public String getCourseTitle(int courseId){
        String sql = "SELECT title FROM courses WHERE course_id = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("title");

        }catch(Exception e){ e.printStackTrace();}
        return null;
    }
    private course mapCourse(ResultSet rs) throws Exception{

        return new course(
                rs.getInt("course_id"),
                rs.getString("code"),
                rs.getString("title"),
                rs.getInt("credits"),
                rs.getString("department"),
                rs.getString("allowed_programs"),
                rs.getInt("min_semester"),
                rs.getDouble("min_cgpa")
        );
    }

    public List<Integer> getPrerequisites(int courseId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT prereq_course_id FROM course_prerequisites WHERE course_id=?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getInt(1));


        }catch(Exception e){ e.printStackTrace(); }
        return list;
    }
    public int addCourse(course c){

        String sql = """
            INSERT INTO courses
            (code, title, credits, department, allowed_programs, min_semester, min_cgpa)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try(Connection conn =ERPDB.getERPConnection();
             PreparedStatement ps =conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,c.getCode());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCredits());
            ps.setString(4,c.getDepartment());
            ps.setString(5,c.getAllowedPrograms());
            ps.setInt(6, c.getMinSemester());
            ps.setDouble(7,c.getMinCgpa());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        }catch(Exception e){e.printStackTrace();}
        return -1;
    }
    public boolean updateCourse(course c){
        String sql = """
            UPDATE courses SET
            code=?, title=?, credits=?, department=?,
            allowed_programs=?, min_semester=?, min_cgpa=?
            WHERE course_id=?
        """;

        try(Connection conn =ERPDB.getERPConnection();
             PreparedStatement ps =conn.prepareStatement(sql)){
            ps.setString(1, c.getCode());
            ps.setString(2,c.getTitle());
            ps.setInt(3,c.getCredits());
            ps.setString(4,c.getDepartment());
            ps.setString(5,c.getAllowedPrograms());
            ps.setInt(6,c.getMinSemester());
            ps.setDouble(7, c.getMinCgpa());
            ps.setInt(8,c.getCourseId());

            return ps.executeUpdate() >0;

        }catch(Exception e){e.printStackTrace();}
        return false;
    }

    public boolean deleteCourse(int courseId){

    try(Connection c =ERPDB.getERPConnection()){
        try(PreparedStatement ps1 =c.prepareStatement(
                "DELETE FROM course_prerequisites WHERE course_id=?")){
            ps1.setInt(1, courseId);
            ps1.executeUpdate();
        }
        try(PreparedStatement ps2 =c.prepareStatement(
                "DELETE FROM course_prerequisites WHERE prereq_course_id=?")){
            ps2.setInt(1, courseId);
            ps2.executeUpdate();
        }
        try(PreparedStatement ps3 =c.prepareStatement(
                "DELETE FROM courses WHERE course_id=?")){
            ps3.setInt(1,courseId);
            return ps3.executeUpdate() >0;
        }
    } catch(Exception e){
        e.printStackTrace();
    }
    return false;}
    public void deleteAllPrerequisites(int courseId){
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(
                     "DELETE FROM course_prerequisites WHERE course_id=?")){
            ps.setInt(1, courseId);
            ps.executeUpdate();

        }catch(Exception e){e.printStackTrace();}
    }

    public void addPrerequisite(int courseId, int prereqCourseId){
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(
                     "INSERT INTO course_prerequisites (course_id, prereq_course_id) VALUES (?, ?)")) {
            ps.setInt(1, courseId);
            ps.setInt(2,prereqCourseId);
            ps.executeUpdate();
        }catch(Exception e){ e.printStackTrace();}
    }
}
