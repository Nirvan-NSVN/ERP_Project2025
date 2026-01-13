package models;

public class user {

    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;            // student / instructor / admin
    private String createdAt;       // timestamp as String

    public user(int userId, String username, String email, String passwordHash,
                String role, String createdAt) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public String getUsername() {
         return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() {
         return passwordHash; }
    public String getRole() { 
        return role; }
    public String getCreatedAt() {
         return createdAt; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { 
        this.email = email; }
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; }
    public void setRole(String role) { 
        this.role = role; }
}
