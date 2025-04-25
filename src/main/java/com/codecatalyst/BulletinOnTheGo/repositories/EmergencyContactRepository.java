package com.codecatalyst.BulletinOnTheGo.repositories;

import com.codecatalyst.BulletinOnTheGo.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    // Find contacts for a specific user ID
    List<EmergencyContact> findByUserId(Long userId);

    // Optional: Find a specific contact by ID and user ID for security checks
    // Optional<EmergencyContact> findByIdAndUserId(Long id, Long userId);
}
