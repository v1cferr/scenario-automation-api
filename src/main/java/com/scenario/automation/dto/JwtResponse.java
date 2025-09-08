package com.scenario.automation.dto;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private Long expiresIn;

    // Construtores
    public JwtResponse() {}

    public JwtResponse(String token, String username, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.expiresIn = expiresIn;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='[PROTECTED]'" +
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
