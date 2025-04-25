package com.codecatalyst.BulletinOnTheGo.service;

import com.codecatalyst.BulletinOnTheGo.dto.EmergencyContactDTO;
import com.codecatalyst.BulletinOnTheGo.entity.EmergencyContact;
import com.codecatalyst.BulletinOnTheGo.entity.User;
import com.codecatalyst.BulletinOnTheGo.exception.ResourceNotFoundException;
import com.codecatalyst.BulletinOnTheGo.repositories.EmergencyContactRepository;
import com.codecatalyst.BulletinOnTheGo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class EmergencyService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;
    public EmergencyService(EmergencyContactRepository emergencyContactRepository,UserRepository userRepository){
        this.userRepository=userRepository;
        this.emergencyContactRepository=emergencyContactRepository;
    }
    private final Logger log = LoggerFactory.getLogger(EmergencyService.class);

    @Transactional(readOnly = true)
    public List<EmergencyContactDTO> getContactsForUser(String userId) {

        log.debug("Fetching contacts for user ID: {}", userId);
        return emergencyContactRepository.findByUserId(userId).stream()
                .map(c -> new EmergencyContactDTO(c.getId(), c.getName(), c.getPhoneNumber()))
                .collect(Collectors.toList());
    }

    @Transactional
    public EmergencyContactDTO addContactForUser(String userId, EmergencyContactDTO contactDTO) {

        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("Cannot add contact for non-existent user with id: " + userId);
        }


        EmergencyContact contact = new EmergencyContact(contactDTO.getName(), contactDTO.getPhoneNumber(), userId);
        EmergencyContact savedContact = emergencyContactRepository.save(contact);
        log.info("Added contact ID {} for user ID {}", savedContact.getId(), userId);

        return new EmergencyContactDTO(savedContact.getId(), savedContact.getName(), savedContact.getPhoneNumber());
    }

    @Transactional
    public void deleteContactForUser(String userId, String contactId) { // Changed IDs to String
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));


        if (!contact.getUserId().equals(userId)) {
            log.warn("User {} attempted to delete contact {} belonging to another user.", userId, contactId);
            throw new SecurityException("User does not have permission to delete this contact.");
        }
        emergencyContactRepository.delete(contact);
        log.info("Deleted contact ID {} for user ID {}", contactId, userId);
    }



    @Transactional(readOnly = true)
    public boolean sendEmergencyAlert(String userId, String userMessage, String location) {
        List<EmergencyContact> contacts = emergencyContactRepository.findByUserId(userId);

        if (contacts.isEmpty()) {
            log.warn("No emergency contacts found for user ID: {}", userId);
            return false;
        }


        String username = userRepository.findById(userId).map(User::getUsername).orElse("UnknownUser");


        String baseMessage = String.format("Emergency alert from user '%s'. ", username);
        baseMessage += userMessage != null && !userMessage.isBlank() ? userMessage : "User needs help.";
        if (location != null && !location.isEmpty() && !location.equalsIgnoreCase("Location unavailable") && !location.equalsIgnoreCase("Geolocation not supported")) {
            baseMessage += " Last known location: " + location;
        }
        final String messageToSend = baseMessage;


        log.info("--- EMERGENCY ALERT TRIGGERED for User ID: {} ---", userId);
        contacts.forEach(contact -> {
            log.info("Simulating sending SMS/alert to {} ({}) for user {}", contact.getName(), contact.getPhoneNumber(), username);
            log.info("Message: {}", messageToSend);

        });
        log.info("--- END EMERGENCY ALERT ---");

        return true;
    }
}