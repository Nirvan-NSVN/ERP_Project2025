package dao;

import database.ERPDB;
import models.enrollment;
import models.section;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sectionDAO {
    public section getSection(int sectionId) {
        String sql =
            "SELECT section_id, course_id, instructor_id, day_time, room, " +
            "capacity, semester, year, grade_locked " +
            "FROM sections WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return new section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("semester"),
                        rs.getInt("year"),
                        rs.getBoolean("grade_locked")
                );
            }

        }catch(Exception e){e.printStackTrace();}
        return null;
    }
    public List<section> getSectionsByInstructor(int instructorId){

        List<section> list = new ArrayList<>();

        String sql =
            "SELECT section_id, course_id, instructor_id, day_time, room, " +
            "capacity, semester, year, grade_locked " +
            "FROM sections WHERE instructor_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(new section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("semester"),
                        rs.getInt("year"),
                        rs.getBoolean("grade_locked")
                ));
            }

        }catch(Exception e){e.printStackTrace();}
        return list;
    }
   
    public List<Integer> getEnrollmentIds(int sectionId) {

        List<Integer> list = new ArrayList<>();

        String sql =
            "SELECT enrollment_id FROM enrollments " +
            "WHERE section_id = ? AND status IN ('active','completed')";

        try(Connection c =ERPDB.getERPConnection();
            PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getInt("enrollment_id"));
        }
        catch(Exception e){e.printStackTrace();}
        return list;
    }
    
    public int getCourseIdBySection(int sectionId) {
        String sql = "SELECT course_id FROM sections WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("course_id");
        } catch(Exception e){e.printStackTrace();}
        return -1;
    }

    public boolean isGradeLocked(int sectionId) {
        String sql = "SELECT grade_locked FROM sections WHERE section_id = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, sectionId);
            ResultSet rs =ps.executeQuery();

            if(rs.next()) return rs.getBoolean("grade_locked");

        }catch(Exception e){ e.printStackTrace();}

        return false;
    }

    public void lockGrades(int sectionId){
        String sql ="UPDATE sections SET grade_locked = 1 WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1,sectionId);
            ps.executeUpdate();

        }catch(Exception e){ e.printStackTrace();}
    }
 
    public void unlockGrades(int sectionId){
        String sql = "UPDATE sections SET grade_locked = 0 WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,sectionId);
            ps.executeUpdate();

        }catch(Exception e){e.printStackTrace();}
    }

    // will print time table for the student
    public List<Object[]> getTimetable(int studentId) {
        List<Object[]> list = new ArrayList<>();

        String sql =
            "SELECT c.code, c.title, s.day_time, s.room, i.name AS instructor " +
            "FROM enrollments e " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "LEFT JOIN instructors i ON s.instructor_id = i.instructor_id " +
            "WHERE e.student_id = ? AND e.status = 'active'";
        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(new Object[]{
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getString("instructor")
                });
            }

        }catch(Exception e){ e.printStackTrace();}
        return list;
    }

    public enrollment getEnrollmentForCourse(int studentId, int courseId) {
        String sql =
            "SELECT e.enrollment_id, e.student_id, e.section_id, e.status " +
            "FROM enrollments e " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "WHERE e.student_id = ? AND s.course_id = ? " +
            "LIMIT 1";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1,studentId);
            ps.setInt(2,courseId);

            ResultSet rs =ps.executeQuery();

            if (rs.next()){
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

    // same section allocation is blocked
    public int getSectionIdByCourseForStudent(int studentId, int courseId) {
        String sql =
            "SELECT e.section_id " +
            "FROM enrollments e " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "WHERE e.student_id = ? AND s.course_id = ? AND e.status = 'active' " +
            "LIMIT 1";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ps.setInt(2,courseId);

            ResultSet rs =ps.executeQuery();
            if(rs.next()) return rs.getInt("section_id");

        } catch(Exception e){e.printStackTrace();}

        return -1;
    }

    public boolean isTempLocked(int sectionId) throws Exception {
        String sql = "SELECT temp_locked FROM sections WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) return rs.getInt(1) ==1;
        }
        return false;
    }

    public void setTempLock(int sectionId, boolean lock) throws Exception{
        String sql = "UPDATE sections SET temp_locked = ? WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, lock ? 1 : 0);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
        }
    }

    public boolean isFinalLocked(int sectionId) throws Exception {
        String sql ="SELECT final_locked FROM sections WHERE section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1) == 1;
        }
        return false;
    }

    public Map<Integer, String[]> getNamesForSection(int sectionId){
        String sql =
            "SELECT e.enrollment_id, s.roll_no, s.name, e.status " +
            "FROM enrollments e " +
            "JOIN students s ON e.student_id = s.student_id " +
            "WHERE e.section_id = ? AND e.status IN ('active', 'completed')";

        Map<Integer, String[]> map =new HashMap<>();

        try(Connection con =ERPDB.getERPConnection();
            PreparedStatement ps =con.prepareStatement(sql)){

            ps.setInt(1,sectionId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                map.put(
                    rs.getInt("enrollment_id"),
                    new String[]{
                        rs.getString("roll_no"),
                        rs.getString("name")
                    }
                );
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return map;
    }

    public int getAnySectionIdForCourse(int courseId){
        String sql = "SELECT section_id FROM sections WHERE course_id=? LIMIT 1";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("section_id");
        } catch(Exception e){ e.printStackTrace();}
        return -1;
    }

    public List<section> getAllSections(){
        List<section> list = new ArrayList<>();

        String sql =
            "SELECT section_id, course_id, instructor_id, day_time, room, " +
            "capacity, semester, year, grade_locked " +
            "FROM sections ORDER BY section_id";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(new section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("semester"),
                        rs.getInt("year"),
                        rs.getBoolean("grade_locked")
                ));
            }

        }catch(Exception e) {e.printStackTrace();}
        return list;
    }


    public boolean createSection(int courseId, int instructorId, String dayTime,String room, int capacity, int semester, int year){

        String sql =
            "INSERT INTO sections(course_id, instructor_id, day_time, room, " +
            "capacity, semester, year, grade_locked, temp_locked, final_locked) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, 0)";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, courseId);
            ps.setInt(2, instructorId);
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setInt(6, semester);
            ps.setInt(7, year);

            return ps.executeUpdate() > 0;
        }catch(Exception e){e.printStackTrace();}
        return false;
    }

    public boolean updateSection(section s) {
        String sql =
            "UPDATE sections SET course_id=?, instructor_id=?, day_time=?, room=?, " +
            "capacity=?, semester=?, year=? WHERE section_id=?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getInstructorId());
            ps.setString(3, s.getDayTime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setInt(6, s.getSemester());
            ps.setInt(7, s.getYear());
            ps.setInt(8, s.getSectionId());

            return ps.executeUpdate() > 0;

        }catch(Exception e) {e.printStackTrace();}
        return false;
    }

    public boolean deleteSection(int sectionId) {

        String sql = "DELETE FROM sections WHERE section_id=?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch(Exception e){e.printStackTrace();}
        return false;
    }

    public Object[] getTimetableRow(int sectionId){
    String sql = """
        SELECT c.code, c.title, s.day_time, s.room,
               COALESCE(i.name, 'TBA') AS instructor
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        LEFT JOIN instructors i ON s.instructor_id = i.instructor_id
        WHERE s.section_id = ?
    """;

    try(Connection conn =ERPDB.getERPConnection();
         PreparedStatement ps =conn.prepareStatement(sql)){
        ps.setInt(1, sectionId);
        ResultSet rs =ps.executeQuery();
        if (rs.next()){
            return new Object[]{
                rs.getString("code"),
                rs.getString("title"),
                rs.getString("day_time"),
                rs.getString("room"),
                rs.getString("instructor")
            };
        }
    } catch(Exception e){
        e.printStackTrace();
    }
    return null;
}

}
