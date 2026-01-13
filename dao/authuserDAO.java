package dao;

import database.DBconnection;
import models.user;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class authuserDAO {
    public int createUser(String username, String email, String passwordHash, String role){
        String sql ="INSERT INTO users(username, email, password_hash, role) VALUES(?, ?, ?, ?)";
        try(Connection c =DBconnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3,passwordHash);
            ps.setString(4, role);
            ps.executeUpdate();
            ResultSet rs =ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    /*  2) UPDATE USER INFO usr can be student and teacher*/
    public boolean updateUser(int userId,String username, String email, String role){
        String sql ="UPDATE users SET username=?, email=?, role=? WHERE user_id=?";
        try (Connection c =DBconnection.getConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2,email);
            ps.setString(3,role);
            ps.setInt(4, userId);
            return ps.executeUpdate() >0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteUser(int userId) {
        String sql ="DELETE FROM users WHERE user_id=?";
        try(Connection c =DBconnection.getConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ps.setInt(1,userId);
            return ps.executeUpdate() >0;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public user getUserById(int userId){
        String sql ="SELECT * FROM users WHERE user_id=?";
        try (Connection c =DBconnection.getConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1,userId);
            ResultSet rs =ps.executeQuery();
            if (rs.next()){
                return mapUser(rs);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //get all user
    public List<user> getAllUsers(){
        List<user> list =new ArrayList<>();
        String sql ="SELECT * FROM users ORDER BY user_id";
        try (Connection c =DBconnection.getConnection();
             PreparedStatement ps =c.prepareStatement(sql)){

            ResultSet rs =ps.executeQuery();
            while (rs.next()){
                list.add(mapUser(rs));}
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public List<user> getUsersByRole(String role){
        List<user> list =new ArrayList<>();
        String sql ="SELECT * FROM users WHERE role=?";
        try (Connection c =DBconnection.getConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs =ps.executeQuery();
            while (rs.next()){
                list.add(mapUser(rs));}
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private user mapUser(ResultSet rs)throws Exception{
        return new user(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("created_at")
        );
    }
}
