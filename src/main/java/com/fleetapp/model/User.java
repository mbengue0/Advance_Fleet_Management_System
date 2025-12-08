package com.fleetapp.model;

public class User {
    private int id;
    private String username;
    private String password; // In a real app, this should be hashed (encrypted)
    private String role; // <--- NEW FIELD


    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUsername() { return username; }
    public String getRole() { return role; }
}