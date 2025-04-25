package com.codecatalyst.BulletinOnTheGo.service;

import com.codecatalyst.BulletinOnTheGo.dto.EmergencyContactDTO;
import com.codecatalyst.BulletinOnTheGo.entity.EmergencyContact;
import com.codecatalyst.BulletinOnTheGo.entity.User;
import com.codecatalyst.BulletinOnTheGo.exception.ResourceNotFoundException;
import com.codecatalyst.BulletinOnTheGo.repositories.EmergencyContactRepository;
import com.codecatalyst.BulletinOnTheGo.repositories.UserRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service // Use constructor injection
public class EmergencyService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;
    public EmergencyService(EmergencyContactRepository emergencyContactRepository,UserRepository userRepository){
        this.userRepository=userRepository;
        this.emergencyContactRepository=emergencyContactRepository;
    }
    private final Logger log = LoggerFactory.getLogger(EmergencyService.class);
    // No explicit constructor needed

    // --- Contact Management ---

    // Note: MongoDB doesn't strictly enforce transactions like SQL DBs across different operations by default.
    // @Transactional might have different behavior. For simple CRUD, it's often less critical unless
    // coordinating multiple repository calls that must succeed or fail together.
    // readOnly=true is primarily an optimization hint for JPA, less impactful for Mongo directly.
    @Transactional(readOnly = true)
    public List<EmergencyContactDTO> getContactsForUser(String userId) { // Changed to String
        // You might fetch the user first if you need to ensure they exist,
        // but often the userId comes from a trusted authenticated context.
        // if (!userRepository.existsById(userId)) {
        //    log.warn("Attempted to get contacts for non-existent user ID: {}", userId);
        //    return List.of();
        // }
        log.debug("Fetching contacts for user ID: {}", userId);
        return emergencyContactRepository.findByUserId(userId).stream() // Uses String ID
                .map(c -> new EmergencyContactDTO(c.getId(), c.getName(), c.getPhoneNumber()))
                .collect(Collectors.toList());
    }

    @Transactional
    public EmergencyContactDTO addContactForUser(String userId, EmergencyContactDTO contactDTO) { // Changed to String
        // Ensure the user actually exists before adding a contact for them.
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("Cannot add contact for non-existent user with id: " + userId);
        }

        // Create contact entity, passing the String userId
        EmergencyContact contact = new EmergencyContact(contactDTO.getName(), contactDTO.getPhoneNumber(), userId);
        EmergencyContact savedContact = emergencyContactRepository.save(contact);
        log.info("Added contact ID {} for user ID {}", savedContact.getId(), userId);
        // DTO ID is now also String
        return new EmergencyContactDTO(savedContact.getId(), savedContact.getName(), savedContact.getPhoneNumber());
    }

    @Transactional
    public void deleteContactForUser(String userId, String contactId) { // Changed IDs to String
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Security check: Ensure the contact belongs to the requesting user
        if (!contact.getUserId().equals(userId)) { // Compare String IDs
            log.warn("User {} attempted to delete contact {} belonging to another user.", userId, contactId);
            // Use a more appropriate security exception if available/needed
            throw new SecurityException("User does not have permission to delete this contact.");
        }
        emergencyContactRepository.delete(contact);
        log.info("Deleted contact ID {} for user ID {}", contactId, userId);
    }

    // --- Alert Logic ---

    // Transactional might not be strictly needed here unless you were also writing alert status to DB
    @Transactional(readOnly = true)
    public boolean sendEmergencyAlert(String userId, String userMessage, String location) { // Changed to String
        List<EmergencyContact> contacts = emergencyContactRepository.findByUserId(userId);

        if (contacts.isEmpty()) {
            log.warn("No emergency contacts found for user ID: {}", userId);
            return false;
        }

        // Fetch username for context (optional but good for logs/message)
        String username = userRepository.findById(userId).map(User::getUsername).orElse("UnknownUser");

        // Construct the message
        String baseMessage = String.format("Emergency alert from user '%s'. ", username);
        baseMessage += userMessage != null && !userMessage.isBlank() ? userMessage : "User needs help.";
        if (location != null && !location.isEmpty() && !location.equalsIgnoreCase("Location unavailable") && !location.equalsIgnoreCase("Geolocation not supported")) {
            baseMessage += " Last known location: " + location;
        }
        final String messageToSend = baseMessage;

        // SIMULATE SENDING
        log.info("--- EMERGENCY ALERT TRIGGERED for User ID: {} ---", userId);
        contacts.forEach(contact -> {
            log.info("Simulating sending SMS/alert to {} ({}) for user {}", contact.getName(), contact.getPhoneNumber(), username);
            log.info("Message: {}", messageToSend);
            // Replace with actual Twilio, SNS, Email API calls
        });
        log.info("--- END EMERGENCY ALERT ---");

        return true;
    }
}