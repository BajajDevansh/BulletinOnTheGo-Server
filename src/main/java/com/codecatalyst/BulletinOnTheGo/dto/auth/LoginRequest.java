package com.codecatalyst.BulletinOnTheGo.dto.auth;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public @NotBlank String getUsername() {
        return username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }
}