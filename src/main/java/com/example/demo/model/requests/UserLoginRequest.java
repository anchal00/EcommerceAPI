package com.example.demo.model.requests;

public class UserLoginRequest {
    private String username;
    private String password;
    
    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
