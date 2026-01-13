package dao;

import database.DBconnection;
import database.ERPDB;
import models.student;
import models.admin;
import models.instructor;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class adminDAO {

    private final authuserDAO authDao = new authuserDAO();
    //student management
    public boolean createStudent(String username, String email, String password, String rollNo, String name,String program, int semester, String hostel){
        try(Connection erp= ERPDB.getERPConnection()){
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            //create user
            int newUserId =authDao.createUser(username, email, hash, "student");
            if (newUserId == -1) return false;
            //insrt into students
            PreparedStatement ps = erp.prepareStatement(
                    "INSERT INTO students(user_id, roll_no, name, program_type, semester, hostel) " +
                    "VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, newUserId);
            ps.setString(2, rollNo);
            ps.setString(3, name);
            ps.setString(4, program);
            ps.setInt(5, semester);
            ps.setString(6, hostel);
            return ps.executeUpdate() > 0;
        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    //update student
    public boolean updateStudent(student s) {
        String sql = "UPDATE students SET roll_no=?, name=?, program_type=?, semester=?, hostel=? WHERE student_id=?";
        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getProgramType());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getHostel());
            ps.setInt(6, s.getStudentId());
            return ps.executeUpdate() > 0;
        }catch (Exception ex){ ex.printStackTrace(); }
        return false;
    }
    public boolean deleteStudent(int studentId, int userId) {
        try(Connection erp = ERPDB.getERPConnection()) {
            deleteStudentDependencies(studentId, erp);
            // delete student
            PreparedStatement ps = erp.prepareStatement("DELETE FROM students WHERE student_id=?" );
            ps.setInt(1, studentId);
            ps.executeUpdate();
            // delete auth user
            authDao.deleteUser(userId);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    private void deleteStudentDependencies(int studentId, Connection erp) throws Exception {
        // Fees
        PreparedStatement ps1 = erp.prepareStatement("DELETE FROM fees WHERE student_id=?");
        ps1.setInt(1, studentId);
        ps1.executeUpdate();
        //Grades from grades table
        PreparedStatement ps2 = erp.prepareStatement(
                "DELETE g FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "WHERE e.student_id=?"
        );
        ps2.setInt(1, studentId);
        ps2.executeUpdate();
        // Enrollments from enrollmnts table
        PreparedStatement ps3 = erp.prepareStatement(
                "DELETE FROM enrollments WHERE student_id=?"
        );
        ps3.setInt(1, studentId);
        ps3.executeUpdate();
        // Semester results
        PreparedStatement ps4 = erp.prepareStatement(
                "DELETE FROM semester_result WHERE student_id=?"
        );
        ps4.setInt(1, studentId);
        ps4.executeUpdate();
        // Timetable
        PreparedStatement ps5 = erp.prepareStatement(
                "DELETE FROM timetable WHERE student_id=?"
        );
        ps5.setInt(1, studentId);
        try { ps5.executeUpdate(); } catch (Exception ignored) {}
        //TA table
        PreparedStatement ps6 = erp.prepareStatement(
                "DELETE FROM tas WHERE student_id=?"
        );
        ps6.setInt(1, studentId);
        ps6.executeUpdate();
    }
    // fetch all ALL STUDENTS 
    public List<student> getAllStudents() {
        List<student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
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
        } catch(Exception ex){ex.printStackTrace();}
        return list;
    }


    // instructor management
    public boolean createInstructor(String username, String email, String password,String name, String department){
        try (Connection erp = ERPDB.getERPConnection()) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            int newUserId = authDao.createUser(username, email, hash, "instructor");
            if (newUserId == -1) return false;
            PreparedStatement ps = erp.prepareStatement("INSERT INTO instructors(user_id, name, department) VALUES (?, ?, ?)");
            ps.setInt(1, newUserId);
            ps.setString(2, name);
            ps.setString(3, department);
            return ps.executeUpdate() > 0;
        } catch(Exception ex){ex.printStackTrace();}
        return false;
    }

    // i have UPDATE INSTRUCTOR if any any instructor department has to be change like Vivek sir from AP to OS
    public boolean updateInstructor(instructor ins) {
        String sql ="UPDATE instructors SET name=?, department=? WHERE instructor_id=?";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ps.setString(1, ins.getName());
            ps.setString(2,ins.getDepartment());
            ps.setInt(3,ins.getInstructorId());
            return ps.executeUpdate() >0;
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    public boolean deleteInstructor(int instructorId, int userId) {
        try(Connection erp =ERPDB.getERPConnection()) {
            PreparedStatement ps =erp.prepareStatement(
                    "DELETE FROM instructors WHERE instructor_id=?"
            );
            ps.setInt(1,instructorId);
            ps.executeUpdate();
            authDao.deleteUser(userId);
            return true;
        } catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    //  this fetch  ALL INSTRUCTORS from the instructor table
    public List<instructor>getAllInstructors(){
        List<instructor> list =new ArrayList<>();
        String sql = "SELECT * FROM instructors ORDER BY instructor_id";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(new instructor(
                        rs.getInt("instructor_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("department")
                ));
            }
        }catch(Exception ex){ex.printStackTrace();}
        return list;
    }
    public ResultSet getAdminSettings() throws Exception{
        Connection c =ERPDB.getERPConnection();
        PreparedStatement ps =c.prepareStatement("SELECT * FROM admin_settings WHERE id=1");
        return ps.executeQuery();
    }
    //registeration lock
    public boolean setRegistrationLock(boolean locked){
        String sql ="UPDATE admin_settings SET registration_locked=? WHERE id=1";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, locked ? 1 : 0);
            return ps.executeUpdate() >0;
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    //maintenance mode
    public boolean setMaintenance(boolean on){
        String sql ="UPDATE admin_settings SET maintenance_mode=? WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ps.setInt(1, on ? 1 : 0);
            return ps.executeUpdate() >0;
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    public boolean isRegistrationLocked(){
        String sql ="SELECT registration_locked FROM admin_settings WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
            PreparedStatement ps =c.prepareStatement(sql)){
            ResultSet rs =ps.executeQuery();
            if (rs.next()){
                return rs.getInt("registration_locked") ==1;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public boolean isMaintenanceMode(){
        String sql ="SELECT maintenance_mode FROM admin_settings WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ResultSet rs =ps.executeQuery();
            return rs.next() && rs.getInt(1) ==1;
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    public boolean setStudentAnnouncement(String msg) {
        String sql = "UPDATE admin_settings SET announcement=? WHERE id=1";
        try (Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, msg);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }
    public boolean setInstructorAnnouncement(String msg) {
        String sql ="UPDATE admin_settings SET instructor_announcement=? WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ps.setString(1, msg);
            return ps.executeUpdate() > 0;
        } catch(Exception ex){ ex.printStackTrace();}
        return false;
    }
    //section final result lock
    public boolean setFinalResultLock(int sectionId, boolean lock) {
        String sql ="UPDATE sections SET final_locked=? WHERE section_id=?";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ps.setInt(1,lock ? 1 : 0);
            ps.setInt(2,sectionId);
            return ps.executeUpdate() >0;
        } 
        catch(Exception ex) { ex.printStackTrace();}

        return false;
    }
    public List<admin> getAllAdmins() {
    List<admin> list = new ArrayList<>();
    String sql = "SELECT user_id, username, email FROM users WHERE role='admin'";
    try (Connection c = DBconnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            list.add(new admin(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email")
            ));
        }
    } catch (Exception e){e.printStackTrace();}
    return list;
}
//creating admin
public boolean createAdmin(String username, String email, String password) {
    try (Connection c = DBconnection.getConnection()) {
        // hash password
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, 'admin')";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, email);
        ps.setString(3, hashed);

        return ps.executeUpdate() > 0;
    } catch(Exception e){
        e.printStackTrace();
    }

    return false;
}


public boolean updateAdmin(admin a) {
    try(Connection c =DBconnection.getConnection()){
        String sql ="UPDATE users SET username=?, email=? WHERE user_id=? AND role='admin'";
        PreparedStatement ps =c.prepareStatement(sql);
        ps.setString(1, a.getUsername());
        ps.setString(2, a.getEmail());
        ps.setInt(4, a.getUserId());
        return ps.executeUpdate() >0;
    }catch (Exception e){e.printStackTrace();}
    return false;
}
public boolean deleteAdmin(int userId) {
    try(Connection c = DBconnection.getConnection()){
        String sql ="DELETE FROM users WHERE user_id=? AND role='admin'";
        PreparedStatement ps =c.prepareStatement(sql);
        ps.setInt(1,userId);
        return ps.executeUpdate() >0;
    }catch(Exception e){e.printStackTrace();}
    return false;
}



}
