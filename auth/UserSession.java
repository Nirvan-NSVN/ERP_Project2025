package auth;

public class UserSession {
    private static int user_id=0;
    private static String username=null;
    private static String role=null;
    public static void setUser(int id, String uname, String r){
        user_id= id;
        username=uname;
        role=r;
    }
    public static int getUserId(){return user_id;}
    public static String getUsername(){
        return username;
    }
    public static String getRole(){
        return role;
    }
    public static void clear() {
        user_id=0;
        username= null;
        role = null;
    }
}
