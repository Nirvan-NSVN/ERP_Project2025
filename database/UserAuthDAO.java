package database;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserAuthDAO {
    public static ResultSet getUserByUsername(String username) throws Exception {
        Connection c = DBconnection.getConnection();

        PreparedStatement st = c.prepareStatement(
            "SELECT user_id, username, password_hash, role FROM users WHERE username = ?"
        );
        st.setString(1, username);

        return st.executeQuery(); // caller will read & close connection
    }

    public static boolean emailExists(String email) throws Exception {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection c = DBconnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public static ResultSet getUserByEmail(String email) throws Exception {
        Connection c = DBconnection.getConnection();

        PreparedStatement st = c.prepareStatement(
            "SELECT user_id, username, password_hash, role, email FROM users WHERE email = ?"
        );
        st.setString(1, email);

        return st.executeQuery(); 
    }

    public static boolean updatePassword(String email, String newPassword) throws Exception {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

        try (Connection c = DBconnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;
        }
    }
}
