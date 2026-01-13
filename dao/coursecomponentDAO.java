package dao;

import database.ERPDB;
import models.coursecomponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class coursecomponentDAO {
    public coursecomponent getComponent(int componentId) {
        String sql =
            "SELECT component_id, offering_id, component_name, weightage, created_by, max_marks " +
            "FROM course_components WHERE component_id = ?";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, componentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return mapComponent(rs);
            }

        }catch(Exception e){ e.printStackTrace();}

        return null;
    }

    public List<coursecomponent> getComponentsByOffering(int offeringId){
        List<coursecomponent> list =new ArrayList<>();
        String sql =
            "SELECT component_id, offering_id, component_name, weightage, created_by, max_marks " +
            "FROM course_components WHERE offering_id = ? ORDER BY component_id";

        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, offeringId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapComponent(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int createComponent(int offeringId, String name, double weightage,int createdBy, double maxMarks) {
        String sql =
            "INSERT INTO course_components(offering_id, component_name, weightage, created_by, max_marks) " +
            "VALUES(?, ?, ?, ?, ?)";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, offeringId);
            ps.setString(2, name);
            ps.setDouble(3, weightage);
            ps.setInt(4, createdBy);
            ps.setDouble(5, maxMarks);
            ps.executeUpdate();
            ResultSet rs =ps.getGeneratedKeys();
            if(rs.next()) return rs.getInt(1);

        } catch(Exception e){ e.printStackTrace();}
        return -1;
    }

    public boolean deleteComponent(int componentId){
    String sql =
        "DELETE FROM course_components " +
        "WHERE component_id = ? AND component_id NOT IN (" +
        "   SELECT component_id FROM student_component_marks" +
        ")";

    try(Connection c =ERPDB.getERPConnection();
         PreparedStatement ps =c.prepareStatement(sql)){

        ps.setInt(1,componentId);
        return ps.executeUpdate() >0;
    } catch(Exception e){e.printStackTrace();}
    return false;
}
    private coursecomponent mapComponent(ResultSet rs) throws Exception{
        return new coursecomponent(
                rs.getInt("component_id"),
                rs.getInt("offering_id"),
                rs.getString("component_name"),
                rs.getDouble("weightage"),
                rs.getInt("created_by"),
                rs.getDouble("max_marks")
        );
    }
}
