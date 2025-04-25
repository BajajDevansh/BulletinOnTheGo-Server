package com.codecatalyst.BulletinOnTheGo.repositories;

import com.codecatalyst.BulletinOnTheGo.entity.EmergencyContact;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmergencyContactRepository extends MongoRepository<EmergencyContact, String> {


    List<EmergencyContact> findByUserId(String userId);

    Optional<EmergencyContact> findByIdAndUserId(String id, String userId);
}
