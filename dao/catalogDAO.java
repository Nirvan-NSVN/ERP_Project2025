package dao;

import database.ERPDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class catalogDAO {

    public List<Object[]>getFullCatalog(){
        List<Object[]> list =new ArrayList<>();
        String sql = """
            SELECT c.code,c.title,s.section_id,i.name AS instructor,s.day_time,s.room,s.capacity
            FROM sections s JOIN courses c ON s.course_id = c.course_id
            LEFT JOIN instructors i ON s.instructor_id = i.instructor_id
            ORDER BY c.code, s.section_id
        """;
        try(Connection con =ERPDB.getERPConnection();
             PreparedStatement ps =con.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new Object[]{
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("section_id"),
                        rs.getString("instructor"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity")
                });
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
