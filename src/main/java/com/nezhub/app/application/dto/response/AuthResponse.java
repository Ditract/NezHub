package com.nezhub.app.application.dto.response;

import com.nezhub.app.domain.enums.UserRole;

public class AuthResponse {

    private String token;
    private String username;
    private UserRole role;

    public AuthResponse(){}

    public AuthResponse(String token, UserRole role, String username) {
        this.token = token;
        this.role = role;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}