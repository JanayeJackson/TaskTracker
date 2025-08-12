package com.example.elevatewebsolutions_tasktracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;
import com.example.elevatewebsolutions_tasktracker.auth.models.UserRole;

import java.util.Objects;

@Entity(tableName = TaskManagerDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password; // This will store the hashed password
    private String passwordSalt; // Salt for password hashing
    private String title;
    private Boolean isAdmin;
    private long createdTimestamp;

    // Constructor for new users (password will be hashed before storing)
    public User(String username, String password, String title) {
        this.username = username;
        this.password = password; // Will be hashed by PasswordUtils before saving
        this.title = title;
        this.isAdmin = false;
        this.createdTimestamp = System.currentTimeMillis();
    }

    // Constructor with admin role
    public User(String username, String password, String title, boolean isAdmin) {
        this.username = username;
        this.password = password; // Will be hashed by PasswordUtils before saving
        this.title = title;
        this.isAdmin = isAdmin;
        this.createdTimestamp = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titile) {
        this.title = title;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    // Helper methods for role management
    public UserRole getUserRole() {
        return UserRole.fromBoolean(isAdmin != null && isAdmin);
    }

    public void setUserRole(UserRole role) {
        this.isAdmin = role.isAdmin();
    }

    public boolean isAdminUser() {
        return isAdmin != null && isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id &&
               createdTimestamp == user.createdTimestamp &&
               Objects.equals(username, user.username) &&
               Objects.equals(password, user.password) &&
               Objects.equals(passwordSalt, user.passwordSalt) &&
               Objects.equals(title, user.title) &&
               Objects.equals(isAdmin, user.isAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, passwordSalt, title, isAdmin, createdTimestamp);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", isAdmin=" + isAdmin +
                ", role=" + getUserRole().getDisplayName() +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
