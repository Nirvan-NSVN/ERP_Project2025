package models;

public class admin {

    private int userId;
    private String username;
    private String email;

    public admin(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    public admin(int userId) {
        this.userId = userId;
    }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
}
