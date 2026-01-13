package database;
import java.sql.*;
public class ERPDB {
    private static final String DB_URL = "jdbc:mysql://mysql-2afd9e51-vikast42.c.aivencloud.com:13280/erp_db";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASS = "";

    public static Connection getERPConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
