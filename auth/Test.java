package auth;
import org.mindrot.jbcrypt.BCrypt;

public class Test {  public static void main(String[] args) {
        String password="user@123";
        String hash=BCrypt.hashpw(password, BCrypt.gensalt(12)); // generate bcrypt hash
        System.out.println("Password: "+ password);
        System.out.println("Bcrypt hash: "+ hash);
        System.out.println("Check result: "+BCrypt.checkpw("user@123",hash));
    }
    
}
