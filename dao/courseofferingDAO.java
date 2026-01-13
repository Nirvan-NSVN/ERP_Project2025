package dao;

import database.ERPDB;
import models.courseoffering;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class courseofferingDAO {
    public courseoffering getOffering(int offeringId){
        String sql = "SELECT offering_id, course_id, semester, year, offered_to_program, created_by " +
                     "FROM course_offerings WHERE offering_id = ?";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1,offeringId);
            ResultSet rs =ps.executeQuery();

            if(rs.next()){
                return mapOffering(rs);
            }
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
    public courseoffering getOffering(int courseId, int semester, int year){
        String sql =
            "SELECT offering_id, course_id, semester, year, offered_to_program, created_by " +
            "FROM course_offerings WHERE course_id = ? AND semester = ? AND year = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1,courseId);
            ps.setInt(2, semester);
            ps.setInt(3,year);

            ResultSet rs =ps.executeQuery();

            if(rs.next()) return mapOffering(rs);
        }catch(Exception e){e.printStackTrace();}

        return null;
    }
    public List<courseoffering> getOfferingsByCourse(int courseId){
        List<courseoffering> list =new ArrayList<>();
        String sql ="SELECT * FROM course_offerings WHERE course_id = ? ORDER BY year DESC, semester DESC";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(mapOffering(rs));

        }catch(Exception e){e.printStackTrace();}
        return list;
    }
    public List<courseoffering> getOfferingsByInstructor(int instructorId){
        List<courseoffering> list =new ArrayList<>();
        String sql ="SELECT * FROM course_offerings WHERE created_by = ?";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1,instructorId);
            ResultSet rs =ps.executeQuery();
            while(rs.next()) list.add(mapOffering(rs));
        }catch(Exception e){e.printStackTrace();}
        return list;
    }
    public int createOffering(int courseId, int semester, int year,
                              String offeredToPrograms, int instructorId) {

        String sql =
            "INSERT INTO course_offerings(course_id, semester, year, offered_to_program, created_by) " +
            "VALUES(?, ?, ?, ?, ?)";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, courseId);
            ps.setInt(2, semester);
            ps.setInt(3, year);
            ps.setString(4, offeredToPrograms);
            ps.setInt(5, instructorId);

            ps.executeUpdate();
            ResultSet rs =ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }catch(Exception e){e.printStackTrace();}

        return -1;
    }
    private courseoffering mapOffering(ResultSet rs) throws Exception{
        return new courseoffering(
                rs.getInt("offering_id"),
                rs.getInt("course_id"),
                rs.getInt("semester"),
                rs.getInt("year"),
                rs.getString("offered_to_program"),
                rs.getInt("created_by")
        );
    }
}
