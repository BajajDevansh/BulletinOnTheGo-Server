package com.codecatalyst.BulletinOnTheGo.dto;

import lombok.Data;

@Data
public class SendAlertRequest {
    // private Long userId; // Needed in a multi-user system
    private String message; // Optional custom message
    private String location; // Optional: "latitude,longitude" or address string

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
