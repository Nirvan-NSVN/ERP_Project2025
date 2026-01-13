package dao;

import database.ERPDB;
import models.grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class gradesDAO {
    public grade getGradeByEnrollment(int enrollmentId){
        String sql = """
            SELECT grade_id, enrollment_id, grade, grade_point
            FROM grades
            WHERE enrollment_id = ?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return mapGrade(rs);

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean addGrade(int enrollmentId, String grade, int gradePoint) {
        String insertSQL = """
            INSERT INTO grades (enrollment_id, grade, grade_point)
            VALUES(?, ?, ?)
        """;

        String updateEnrollment = """
            UPDATE enrollments
            SET status='completed'
            WHERE enrollment_id=?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement add =c.prepareStatement(insertSQL);
             PreparedStatement up =c.prepareStatement(updateEnrollment)){

            // Insert grade
            add.setInt(1, enrollmentId);
            add.setString(2, grade);
            add.setInt(3, gradePoint);
            add.executeUpdate();

            // Mark as completed
            up.setInt(1, enrollmentId);
            up.executeUpdate();
            return true;

        }catch(SQLIntegrityConstraintViolationException dup){
            return false;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateGrade(int enrollmentId, String grade, int gradePoint){

        String sql = """
            UPDATE grades
            SET grade=?, grade_point=?
            WHERE enrollment_id=?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setString(1,grade);
            ps.setInt(2, gradePoint);
            ps.setInt(3,enrollmentId);
            return ps.executeUpdate() >0;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteGrade(int enrollmentId){
        String deleteSQL ="DELETE FROM grades WHERE enrollment_id=?";
        String restoreSQL ="UPDATE enrollments SET status='active' WHERE enrollment_id=?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement del =c.prepareStatement(deleteSQL);
             PreparedStatement res =c.prepareStatement(restoreSQL)){
            del.setInt(1,enrollmentId);
            boolean deleted =del.executeUpdate() > 0;

            if(deleted){
                // restore enrollment
                res.setInt(1, enrollmentId);
                res.executeUpdate();
            }
            return deleted;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public List<grade> getGradesForStudent(int studentId){

        List<grade> list =new ArrayList<>();

        String sql = """
            SELECT g.grade_id, g.enrollment_id, g.grade, g.grade_point
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
        """;
        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
                list.add(mapGrade(rs));
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
    private grade mapGrade(ResultSet rs) throws Exception {
        return new grade(
                rs.getInt("grade_id"),
                rs.getInt("enrollment_id"),
                rs.getString("grade"),
                rs.getInt("grade_point")
        );
    }
    // DELETE ALL GRADES OF A SECTION yeh maine aise hi bana diya ki agar mainataince ke chakar mein webiste ko dobara shuru hona pada to fir delte 
    public void deleteGradesOfSection(int sectionId) throws Exception {
        String sql = """
            DELETE g
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }
}
