package com.codecatalyst.BulletinOnTheGo.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class EmergencyContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ****** ADDED: Link to User ******
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch is generally good practice
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmergencyContact(String name, String phoneNumber, User user) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    public EmergencyContact(){}

}