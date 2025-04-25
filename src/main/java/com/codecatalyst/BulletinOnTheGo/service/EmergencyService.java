package com.codecatalyst.BulletinOnTheGo.service;

import com.codecatalyst.BulletinOnTheGo.dto.EmergencyContactDTO;
import com.codecatalyst.BulletinOnTheGo.entity.EmergencyContact;
import com.codecatalyst.BulletinOnTheGo.entity.User;
import com.codecatalyst.BulletinOnTheGo.repositories.EmergencyContactRepository;
import com.codecatalyst.BulletinOnTheGo.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class EmergencyService {
    private static final org. slf4j. Logger log
            = org. slf4j. LoggerFactory. getLogger(EmergencyService.class);
    private final EmergencyContactRepository emergencyContactRepository;
    @Autowired
    public EmergencyService(EmergencyContactRepository emergencyContactRepository,UserRepository userRepository){
        this.emergencyContactRepository=emergencyContactRepository;
        this.userRepository=userRepository;
    }
    private final UserRepository userRepository; // Inject UserRepository

    // Remove @PostConstruct dummy data - contacts should be user-specific

    @Transactional(readOnly = true) // Good practice for read operations
    public List<EmergencyContactDTO> getContactsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Attempted to get contacts for non-existent user ID: {}", userId);
            // Or throw specific exception
            return List.of(); // Return empty list
        }
        return emergencyContactRepository.findByUserId(userId).stream()
                .map(c -> new EmergencyContactDTO(c.getId(), c.getName(), c.getPhoneNumber()))
                .collect(Collectors.toList());
    }

    @Transactional // Write operation
    public EmergencyContactDTO addContactForUser(Long userId, EmergencyContactDTO contactDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        EmergencyContact contact = new EmergencyContact(contactDTO.getName(), contactDTO.getPhoneNumber(), user);
        EmergencyContact savedContact = emergencyContactRepository.save(contact);
        return new EmergencyContactDTO(savedContact.getId(), savedContact.getName(), savedContact.getPhoneNumber());
    }

    @Transactional(readOnly = true) // Read operation
    public boolean sendEmergencyAlert(Long userId, String userMessage, String location) {
        // 1. Fetch user's emergency contacts
        List<EmergencyContact> contacts = emergencyContactRepository.findByUserId(userId);

        if (contacts.isEmpty()) {
            log.warn("No emergency contacts found for user ID: {}", userId);
            return false; // Or throw an exception
        }

        // Fetch username for context (optional)
        String username = userRepository.findById(userId).map(User::getUsername).orElse("UnknownUser");


        // 2. Construct the message
        String baseMessage = String.format("Emergency alert from user '%s'. ", username);
        baseMessage += userMessage != null && !userMessage.isBlank() ? userMessage : "User needs help.";
        if (location != null && !location.isEmpty() && !location.equalsIgnoreCase("Location unavailable") && !location.equalsIgnoreCase("Geolocation not supported")) {
            baseMessage += " Last known location: " + location;
        }
        final String messageToSend = baseMessage;

        // 3. *** SIMULATE SENDING ***
        log.info("--- EMERGENCY ALERT TRIGGERED for User ID: {} ---", userId);
        contacts.forEach(contact -> {
            log.info("Simulating sending SMS/alert to {} ({}) for user {}", contact.getName(), contact.getPhoneNumber(), username);
            log.info("Message: {}", messageToSend);
        });
        log.info("--- END EMERGENCY ALERT ---");

        return true;
    }

    // Optional: Add method to delete contact securely
    @Transactional
    public void deleteContactForUser(Long userId, Long contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));

        // Security check: Ensure the contact belongs to the requesting user
        if (!contact.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to delete contact {} belonging to another user.", userId, contactId);
            throw new SecurityException("User does not have permission to delete this contact.");
        }
        emergencyContactRepository.delete(contact);
        log.info("Deleted contact ID {} for user ID {}", contactId, userId);
    }
}