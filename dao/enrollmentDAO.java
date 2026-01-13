package dao;

import database.ERPDB;
import models.enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class enrollmentDAO {
    public enrollment getEnrollment(int enrollmentId) {
        String sql = """
            SELECT enrollment_id, student_id, section_id, status
            FROM enrollments
            WHERE enrollment_id = ?
        """;
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                );
            }
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    public List<enrollment> getAllEnrollments(int studentId){

        List<enrollment> list =new ArrayList<>();

        String sql = """
            SELECT enrollment_id, student_id, section_id, status
            FROM enrollments
            WHERE student_id = ?
            ORDER BY enrollment_id ASC
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ResultSet rs =ps.executeQuery();

            while(rs.next()){
                list.add(new enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                ));
            }

        }catch(Exception e){e.printStackTrace();}
        return list;
    }

    public enrollment getLatestRecord(int studentId, int courseId){

        String sql = """
            SELECT e.enrollment_id, e.student_id, e.section_id, e.status
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            WHERE e.student_id=? AND s.course_id=?
            ORDER BY e.enrollment_id DESC
            LIMIT 1
        """;
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                );
            }

        }catch(Exception e){e.printStackTrace();}
        return null;
    }
  
    public enrollment getEnrollmentForCourse(int studentId, int courseId){
        return getLatestRecord(studentId, courseId);
    }

    public boolean isEnrollmentGraded(int enrollmentId){
        String sql ="SELECT 1 FROM grades WHERE enrollment_id=? LIMIT 1";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, enrollmentId);
            return ps.executeQuery().next();

        }catch(Exception e){e.printStackTrace();}

        return false;
    }

    public boolean isCompleted(int studentId, int courseId){
        String sql = """
            SELECT 1
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            WHERE e.student_id=? AND s.course_id=? AND e.status='completed'
            LIMIT 1
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeQuery().next();

        }catch(Exception e){e.printStackTrace();}
        return false;
    }
    public boolean hasActive(int studentId, int courseId){
        String sql = """
            SELECT 1
            FROM enrollments e
            JOIN sections s ON e.section_id=s.section_id
            WHERE e.student_id=? AND s.course_id=? AND e.status='active'
            LIMIT 1
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2,courseId);
            return ps.executeQuery().next();

        }catch(Exception e){e.printStackTrace();}

        return false;
    }
    public boolean addEnrollment(int studentId, int sectionId){
        String sql = """
            INSERT INTO enrollments(student_id, section_id, status)
            VALUES (?, ?, 'active')
        """;
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ps.setInt(2,sectionId);

            return ps.executeUpdate() >0;
        }catch(Exception e){e.printStackTrace();}

        return false;
    }
    public boolean dropEnrollment(int enrollmentId){
        String sql = """
            UPDATE enrollments
            SET status='dropped'
            WHERE enrollment_id=?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() >0;
        }
        catch(Exception e){e.printStackTrace();}

        return false;
    }

    public List<enrollment> getActiveEnrollments(int studentId){
        List<enrollment> list = new ArrayList<>();
        String sql = """
            SELECT *
            FROM enrollments
            WHERE student_id=? AND status='active'
        """;

        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(new enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                ));
            }
        }
        catch(Exception e){e.printStackTrace();}

        return list;
    }
}
