package auth;
import database.UserAuthDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.ResultSet;

public class AuthService {
    //authenticate
    public static boolean authenticate(String username, String password) {
        try{
            ResultSet rs =UserAuthDAO.getUserByUsername(username);
            if (!rs.next()){ return false;}
            int userId=rs.getInt("user_id");
            String dbUser=rs.getString("username");
            String hash=rs.getString("password_hash");
            String role=rs.getString("role");
            if (!BCrypt.checkpw(password, hash)){return false;}
            UserSession.setUser(userId, dbUser, role);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //check email
    public static boolean emailExists(String email) {
        try{
            ResultSet rs=UserAuthDAO.getUserByEmail(email);
            return rs.next();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    // reset passwrd
    public static boolean updatePassword(String email,String newPassword) {
        try{
            String hashed=BCrypt.hashpw(newPassword,BCrypt.gensalt());
            return UserAuthDAO.updatePassword(email,hashed);}
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
