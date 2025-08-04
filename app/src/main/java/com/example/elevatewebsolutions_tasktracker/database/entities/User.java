package com.example.elevatewebsolutions_tasktracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerDatabase;

import java.util.Objects;

@Entity(tableName = TaskManagerDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private String title;

    private Boolean isAdmin;

    public User(String username, String password, String title) {
        this.username = username;
        this.password = password;
        this.title = title;
        isAdmin = false;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(title, user.title) && Objects.equals(isAdmin, user.isAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, title, isAdmin);
    }
}
