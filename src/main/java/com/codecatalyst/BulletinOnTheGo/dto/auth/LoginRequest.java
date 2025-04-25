package com.codecatalyst.BulletinOnTheGo.dto.auth;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // This annotation generates getters, setters, toString, etc.
public class LoginRequest {
    @NotBlank
    private String username; // Field is named 'username'

    @NotBlank
    private String password; // Field is named 'password'

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