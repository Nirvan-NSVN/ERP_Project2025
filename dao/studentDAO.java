package dao;

import database.ERPDB;
import models.student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class studentDAO {
    //(used in login/dashboard)
    public student getStudentByUserId(int userId) {
        String sql = "SELECT student_id, user_id, roll_no, name, program_type, semester, hostel " +
                     "FROM students WHERE user_id = ?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("program_type"),
                        rs.getInt("semester"),
                        rs.getString("hostel")
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
   
    public int getStudentId(int userId) {
        String sql = "SELECT student_id FROM students WHERE user_id = ?";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("student_id");

        } catch(Exception e){
            e.printStackTrace();
        }

        return -1;
    }
    // GET ALL STUDENTS (used by Admin Dashboard)
public List<student> getAllStudents() {
    List<student> list = new ArrayList<>();

    String sql =
        "SELECT student_id, user_id, roll_no, name, program_type, semester, hostel " +
        "FROM students ORDER BY student_id";
        try (Connection c =ERPDB.getERPConnection();
            PreparedStatement ps =c.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("program_type"),
                        rs.getInt("semester"),
                        rs.getString("hostel")
                ));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public double getCGPA(int studentId) {
        String sql = "SELECT AVG(sgpa) AS cgpa FROM semester_result WHERE student_id = ?";

        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("cgpa");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public List<Integer> getSemesters(int studentId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM semester_result " +
                     "WHERE student_id = ? ORDER BY semester";

        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(rs.getInt("semester"));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    //  GET RESULT OF A SEMESTER (for dashboar)
    public List<Object[]> getSemesterResult(int studentId, int semester) {
        List<Object[]> list = new ArrayList<>();

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
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("grade"),
                        rs.getInt("grade_point")
                });
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
    //  GET BASIC STUDENT INFO FOR csv
    public student getStudentBasic(int studentId) {
        String sql = "SELECT student_id, roll_no, name, program_type, semester, hostel " +
                     "FROM students WHERE student_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new student(
                        rs.getInt("student_id"),
                        -1,  // user_id not needed for csv
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("program_type"),
                        rs.getInt("semester"),
                        rs.getString("hostel")
                );
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
