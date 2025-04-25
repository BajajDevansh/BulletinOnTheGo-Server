package com.codecatalyst.BulletinOnTheGo.dto.auth;


import lombok.Data;


@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id; // <-- Changed to String
    private String username;
    private String email;


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


    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JwtResponse(String accessToken, String id, String username, String email) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }
    public JwtResponse(){}
}
