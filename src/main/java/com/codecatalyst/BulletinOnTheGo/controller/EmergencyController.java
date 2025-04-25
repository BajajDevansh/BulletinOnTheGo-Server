package com.codecatalyst.BulletinOnTheGo.controller;

import com.codecatalyst.BulletinOnTheGo.dto.EmergencyContactDTO;
import com.codecatalyst.BulletinOnTheGo.dto.SendAlertRequest;
import com.codecatalyst.BulletinOnTheGo.dto.message.MessageResponse;
import com.codecatalyst.BulletinOnTheGo.exception.ResourceNotFoundException;
import com.codecatalyst.BulletinOnTheGo.security.UserDetailsImpl;
import com.codecatalyst.BulletinOnTheGo.service.EmergencyService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/emergency")
// @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // Prefer global CORS config
public class EmergencyController {
    private final Logger log= LoggerFactory.getLogger(EmergencyController.class);
    private final EmergencyService emergencyService;
    // No explicit constructor needed
    public EmergencyController(EmergencyService emergencyService){
        this.emergencyService=emergencyService;
    }
    // Helper method to get current user's ID (now returns String)
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            // Consider throwing a more specific Spring Security exception if appropriate
            throw new SecurityException("User not authenticated");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId(); // Returns String ID
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendAlert(@RequestBody SendAlertRequest request) {
        String userId = getCurrentUserId(); // Get String ID
        boolean success = emergencyService.sendEmergencyAlert(userId, request.getMessage(), request.getLocation());
        if (success) {
            return ResponseEntity.ok(new MessageResponse("Alert triggered successfully (simulated)"));
        } else {
            return ResponseEntity.status(500).body(new MessageResponse("Failed to trigger alert. Ensure contacts are configured."));
        }
    }

    @GetMapping("/contacts")
    @PreAuthorize("isAuthenticated()")
    public List<EmergencyContactDTO> getContacts() {
        String userId = getCurrentUserId(); // Get String ID
        return emergencyService.getContactsForUser(userId);
    }

    @PostMapping("/contacts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addContact(@RequestBody EmergencyContactDTO contactDTO) { // Changed return to ResponseEntity<?> for consistency
        String userId = getCurrentUserId(); // Get String ID
        // Basic validation
        if (contactDTO.getName() == null || contactDTO.getName().isBlank() ||
                contactDTO.getPhoneNumber() == null || contactDTO.getPhoneNumber().isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Name and Phone Number are required."));
        }
        try {
            EmergencyContactDTO newContact = emergencyService.addContactForUser(userId, contactDTO);
            return ResponseEntity.ok(newContact);
        } catch (UsernameNotFoundException e) { // Catch if user doesn't exist from service
            return ResponseEntity.status(404).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            // Log the exception e.printStackTrace(); or log.error("...", e);
            return ResponseEntity.status(500).body(new MessageResponse("Error adding contact."));
        }
    }

    @DeleteMapping("/contacts/{contactId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteContact(@PathVariable String contactId) {
        String userId = getCurrentUserId();
        try {
            emergencyService.deleteContactForUser(userId, contactId);
            log.info("Successfully deleted contact {} for user {}", contactId, userId); // Added success log
            return ResponseEntity.ok(new MessageResponse("Contact deleted successfully."));

        } catch (ResourceNotFoundException e) { // <<< CATCH ResourceNotFoundException
            log.warn("Attempt to delete non-existent contact {} for user {}: {}", contactId, userId, e.getMessage()); // Log the warning
            // Use HttpStatus.NOT_FOUND for clarity
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: Contact not found."));

        } catch (SecurityException e) { // Catch the specific exception from the service
            log.warn("Forbidden attempt by user {} to delete contact {}: {}", userId, contactId, e.getMessage()); // Log the warning
            // Use HttpStatus.FORBIDDEN for clarity
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Forbidden."));

        } catch (Exception e) { // Catch unexpected errors
            // Log the full stack trace for unexpected errors at ERROR level
            log.error("Unexpected error deleting contact {} for user {}", contactId, userId, e);
            // Use HttpStatus.INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error: Could not delete contact due to an internal server error."));
        }
    }
}