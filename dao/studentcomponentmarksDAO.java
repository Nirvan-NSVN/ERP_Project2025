package dao;

import database.ERPDB;
import models.studentcomponentmarks;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class studentcomponentmarksDAO {
    public studentcomponentmarks getMarks(int enrollmentId, int componentId) {
        String sql =
            "SELECT enrollment_id, component_id, marks_obtained, max_marks, graded_by, grade_time " +
            "FROM student_component_marks WHERE enrollment_id = ? AND component_id = ?";

        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, componentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapMarks(rs);
            }

        } catch(Exception e){e.printStackTrace();}
        return null;
    }


    // GET ALL MARKS FOR AN ENROLLMENT (used for grade calculation)
    public List<studentcomponentmarks> getMarksForEnrollment(int enrollmentId) {
        List<studentcomponentmarks> list = new ArrayList<>();

        String sql =
            "SELECT enrollment_id, component_id, marks_obtained, max_marks, graded_by, grade_time " +
            "FROM student_component_marks WHERE enrollment_id = ?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapMarks(rs));

        }catch(Exception e) {e.printStackTrace();}

        return list;
    }


    // GET MARKS FOR ONE COMPONENT (all students in a section)
    public List<studentcomponentmarks> getMarksForComponent(int componentId){

        List<studentcomponentmarks> list = new ArrayList<>();
        String sql =
            "SELECT enrollment_id, component_id, marks_obtained, max_marks, graded_by, grade_time " +
            "FROM student_component_marks WHERE component_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1, componentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapMarks(rs));

        } catch(Exception e){e.printStackTrace();}

        return list;
    }


    public boolean addMarks(int enrollmentId, int componentId,double marksObtained, double maxMarks, int gradedBy) {

        String sql =
            "INSERT INTO student_component_marks(enrollment_id, component_id, marks_obtained, max_marks, graded_by, grade_time) " +
            "VALUES(?, ?, ?, ?, ?, NOW())";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setInt(2, componentId);
            ps.setDouble(3, marksObtained);
            ps.setDouble(4, maxMarks);
            ps.setInt(5, gradedBy);

            return ps.executeUpdate() > 0;

        }catch(Exception e){e.printStackTrace();}
        return false;
    }


    public boolean updateMarks(int enrollmentId, int componentId,double marksObtained, double maxMarks,int gradedBy) {
        String sql =
            "UPDATE student_component_marks " +
            "SET marks_obtained = ?, max_marks = ?, graded_by = ?, grade_time = NOW() " +
            "WHERE enrollment_id = ? AND component_id = ?";
            try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, marksObtained);
            ps.setDouble(2, maxMarks);
            ps.setInt(3, gradedBy);
            ps.setInt(4, enrollmentId);
            ps.setInt(5, componentId);

            return ps.executeUpdate() > 0;

        } 
        catch(Exception e) {e.printStackTrace();}

        return false;
    }


    // DELETE MARKS (rare but needed for CSV import overwrite)
    public boolean deleteMarks(int enrollmentId, int componentId) {

        String sql =
            "DELETE FROM student_component_marks WHERE enrollment_id = ? AND component_id = ?";

        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, componentId);

            return ps.executeUpdate() > 0;

        } 
        catch (Exception e) {
             e.printStackTrace(); }

        return false;
    }

    private studentcomponentmarks mapMarks(ResultSet rs) throws Exception {
        return new studentcomponentmarks(
                rs.getInt("enrollment_id"),
                rs.getInt("component_id"),
                rs.getDouble("marks_obtained"),
                rs.getDouble("max_marks"),
                rs.getInt("graded_by"),
                String.valueOf(rs.getTimestamp("grade_time"))
        );
    }
    public PreparedStatement getBatchSaveStatement(Connection c) throws Exception {
        String sql = """
            INSERT INTO student_component_marks
            (enrollment_id, component_id, marks_obtained, max_marks, graded_by, grade_time)
            VALUES (?, ?, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                marks_obtained = VALUES(marks_obtained),
                max_marks = VALUES(max_marks),
                graded_by = VALUES(graded_by),
                grade_time = NOW()
        """;

        return c.prepareStatement(sql);
    }

    public void addToBatch(PreparedStatement ps,
                        int enrollmentId,
                        int componentId,
                        double marks,
                        double maxMarks,
                        int instructorId) throws Exception {

        ps.setInt(1, enrollmentId);
        ps.setInt(2, componentId);
        ps.setDouble(3, marks);
        ps.setDouble(4, maxMarks);
        ps.setInt(5, instructorId);

        ps.addBatch();
    }


    // Crash-safe delete for final lock
    public void deleteMarksOfSection(int sectionId) throws Exception {
        String sql = "DELETE scm FROM student_component_marks scm " +
                     "JOIN enrollments e ON scm.enrollment_id = e.enrollment_id " +
                     "WHERE e.section_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }
    public Map<Integer, Map<Integer, Double>> getAllMarksForSection(int sectionId) {
    String sql = """
        SELECT e.enrollment_id, m.component_id, m.marks_obtained
        FROM student_component_marks m
        JOIN enrollments e ON m.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
    """;

    Map<Integer, Map<Integer, Double>> result = new HashMap<>();

    try(Connection c = ERPDB.getERPConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, sectionId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            int enr  = rs.getInt("enrollment_id");
            int comp = rs.getInt("component_id");
            double marks = rs.getDouble("marks_obtained");
            result
                .computeIfAbsent(enr, k -> new HashMap<>())
                .put(comp, marks);
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }

    return result;
}
}
