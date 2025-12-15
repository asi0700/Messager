package com.example.first_project.network.dto;

public class AuthRequest {
    public String email;
    public String password;
    public String username;

    public AuthRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

