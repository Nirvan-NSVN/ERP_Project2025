package dao;

import database.ERPDB;
import models.enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class registrationDAO {

    private final enrollmentDAO enrDao = new enrollmentDAO();
    public boolean isRegistrationLocked() {
        String sql = "SELECT registration_locked FROM admin_settings WHERE id=1";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("registration_locked") == 1;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;   //  default = unlocked
    }
    public boolean isMaintenanceMode(){
        String sql ="SELECT maintenance_mode FROM admin_settings WHERE id=1";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ResultSet rs =ps.executeQuery();
            if(rs.next())
                return rs.getInt("maintenance_mode") ==1;
        } catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public int findAvailableSection(int courseId) {

        String sql = """
            SELECT section_id,
                   COALESCE(capacity, 999999) AS cap
            FROM sections
            WHERE course_id=?
            ORDER BY section_id
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getInt("cap") > 0)
                    return rs.getInt("section_id");
            }

        } catch(Exception e){ e.printStackTrace();}

        return -1;
    }

    public boolean registerCourse(int studentId, int courseId) {
        if (isRegistrationLocked() || isMaintenanceMode())
            return false;
        // cannot register completed course
        if (enrDao.isCompleted(studentId, courseId))
            return false;

        // if already active
        if (enrDao.hasActive(studentId, courseId))
            return false;

        int sectionId = findAvailableSection(courseId);
        if (sectionId == -1) return false;

        return enrDao.addEnrollment(studentId, sectionId);
    }

    public boolean dropCourse(int studentId, int courseId) {

        //  stop if locked
        if (isRegistrationLocked() || isMaintenanceMode())
            return false;

        enrollment last = enrDao.getLatestRecord(studentId, courseId);
        if (last == null) return false;

        int eid = last.getEnrollmentId();
        int sectionId = last.getSectionId();

        if ("completed".equals(last.getStatus()))
            return false;

        if (!"active".equals(last.getStatus()))
            return false;

        if (enrDao.isEnrollmentGraded(eid))
            return false;

        boolean ok = enrDao.dropEnrollment(eid);

        if (ok)
            restoreSectionCapacity(sectionId);

        return ok;
    }

    private void restoreSectionCapacity(int sectionId) {
        String sql = """
            UPDATE sections
            SET capacity = CASE
                WHEN capacity IS NULL THEN NULL
                ELSE capacity + 1
            END
            WHERE section_id=?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ps.executeUpdate();

        }catch(Exception e){e.printStackTrace();}
    }

    public List<Integer> getActiveCourseIds(int studentId) {

        List<Integer> list = new ArrayList<>();

        String sql = """
            SELECT s.course_id
            FROM enrollments e
            JOIN sections s ON e.section_id=s.section_id
            WHERE e.student_id=? AND e.status='active'
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(rs.getInt(1));

        } 
        catch(Exception e){ e.printStackTrace();}
        return list;
    }

    // this check if pending then the course is not taken
    public boolean hasCompletedOrTakenBefore(int studentId, int courseId){

    String sql = """
        SELECT e.status
        FROM enrollments e
        JOIN sections s ON e.section_id = s.section_id
        WHERE e.student_id=? AND s.course_id=?
        ORDER BY e.enrollment_id DESC
        LIMIT 1
    """;

    try(Connection c =ERPDB.getERPConnection();
         PreparedStatement ps =c.prepareStatement(sql)){
        ps.setInt(1, studentId);
        ps.setInt(2, courseId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()){
            String status =rs.getString("status");
            //  Block if completed
            if (status.equals("completed"))
                return true;

            //  Block if currently taken
            if (status.equals("active"))
                return true;

            //  Pending allowed
            if (status.equals("pending"))
                return false;

            //  Dropped allowed
            if (status.equals("dropped"))
                return false;
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return false;
} 
    public List<Integer> getCoursesTakenBefore(int studentId) {

        List<Integer> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT s.course_id
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            WHERE e.student_id=?
        """;

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1,studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(rs.getInt(1));

        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

}
