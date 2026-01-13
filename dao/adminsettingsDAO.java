package dao;
import database.ERPDB;
import java.sql.*;

public class adminsettingsDAO {
    // Ensure the admin_settings row exists (id = 1)
    private void ensureRowExists() {
        String sql = "INSERT INTO admin_settings (id, registration_locked, maintenance_mode, announcement, instructor_announcement) " +
                     "VALUES (1, 0, 0, '', '') " +
                     "ON DUPLICATE KEY UPDATE id = id";
        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private boolean getFlag(String column){
        ensureRowExists();
        String sql ="SELECT "+column+" FROM admin_settings WHERE id=1";
        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)) {
            ResultSet rs =ps.executeQuery();
            if (rs.next()) return rs.getInt(1) ==1;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private String getText(String column){
        ensureRowExists();
        String sql ="SELECT " + column + " FROM admin_settings WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ResultSet rs =ps.executeQuery();
            if(rs.next()) return rs.getString(1) == null ? "" : rs.getString(1);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private boolean updateFlag(String column, boolean val){
        ensureRowExists();
        String sql ="UPDATE admin_settings SET " + column + "=? WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setInt(1, val ? 1 : 0);
            return ps.executeUpdate() >0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateText(String column, String text){
        ensureRowExists();
        String sql ="UPDATE admin_settings SET " + column + "=? WHERE id=1";
        try (Connection c =ERPDB.getERPConnection();
             PreparedStatement ps =c.prepareStatement(sql)){
            ps.setString(1, text);
            return ps.executeUpdate() >0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    // Public API
    public boolean isRegistrationLocked(){
        return getFlag("registration_locked");
    }
    public boolean isMaintenanceMode(){
        return getFlag("maintenance_mode");
    }
    public String getStudentAnnouncement(){
        return getText("announcement");
    }
    
    public String getInstructorAnnouncement(){
        return getText("instructor_announcement");
    }
    public boolean setRegistrationLocked(boolean lock){
        return updateFlag("registration_locked", lock);
    }
    public boolean setMaintenanceMode(boolean on){
        return updateFlag("maintenance_mode", on);
    }

    public boolean setStudentAnnouncement(String text){
        return updateText("announcement", text);
    }
    public boolean setInstructorAnnouncement(String text){
        return updateText("instructor_announcement", text);
    }
}
