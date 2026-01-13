package dao;

import database.ERPDB;
import models.instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class instructorDAO {
    public instructor getInstructorByUserId(int userId) {
        String sql =
            "SELECT instructor_id, user_id, name, department " +
            "FROM instructors WHERE user_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new instructor(
                        rs.getInt("instructor_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("department")
                );
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    public int getInstructorId(int userId){
        String sql = "SELECT instructor_id FROM instructors WHERE user_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("instructor_id");
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public instructor getInstructor(int instructorId){
        String sql =
            "SELECT instructor_id, user_id, name, department " +
            "FROM instructors WHERE instructor_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new instructor(
                        rs.getInt("instructor_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("department")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getInstructorName(int instructorId){
        String sql ="SELECT name FROM instructors WHERE instructor_id = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");

        }catch(Exception e){
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String getInstructorDepartment(int instructorId){
        String sql = "SELECT department FROM instructors WHERE instructor_id = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("department");
        }catch(Exception e){
            e.printStackTrace();
        }
        return "Unknown";
    }
    //GET ALL INSTRUCTORS Used in Admin Dashboard because admin dash board mein dikkat aa rahi thi 
public List<instructor> getAllInstructors() {
    List<instructor> list = new ArrayList<>();

    String sql =
        "SELECT instructor_id, user_id, name, department " +
        "FROM instructors ORDER BY instructor_id";

    try(Connection c =ERPDB.getERPConnection();
         PreparedStatement ps =c.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            list.add(new instructor(
                    rs.getInt("instructor_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("department")
            ));
        }

    }catch(Exception e){
        e.printStackTrace();
    }
    return list;
}

}
