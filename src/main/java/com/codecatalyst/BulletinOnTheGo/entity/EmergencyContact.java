package com.codecatalyst.BulletinOnTheGo.entity;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "emergency_contacts")
@Data
public class EmergencyContact {
    @Id
    private String id;

    public @NotBlank String getUserId() {
        return userId;
    }

    public void setUserId(@NotBlank String userId) {
        this.userId = userId;
    }

    @Indexed
    @NotBlank
    private String userId;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EmergencyContact(String name, String phoneNumber, String userId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userId=userId;
    }

    public EmergencyContact(){}

}