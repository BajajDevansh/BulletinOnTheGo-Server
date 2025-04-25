package com.codecatalyst.BulletinOnTheGo.controller;

import com.codecatalyst.BulletinOnTheGo.dto.EmergencyContactDTO;
import com.codecatalyst.BulletinOnTheGo.dto.SendAlertRequest;
import com.codecatalyst.BulletinOnTheGo.dto.message.MessageResponse;
import com.codecatalyst.BulletinOnTheGo.security.UserDetailsImpl;
import com.codecatalyst.BulletinOnTheGo.service.EmergencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // Adjust CORS as needed
public class EmergencyController {

    private final EmergencyService emergencyService;

    @Autowired // Optional on single constructor in recent Spring versions
    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService; // Initialize here
    }

    // Helper method to get current user's ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new SecurityException("User not authenticated"); // Or handle differently
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()") // Ensure user is logged in
    public ResponseEntity<?> sendAlert(@RequestBody SendAlertRequest request) {
        Long userId = getCurrentUserId();
        boolean success = emergencyService.sendEmergencyAlert(userId, request.getMessage(), request.getLocation());
        if (success) {
            return ResponseEntity.ok(new MessageResponse("Alert triggered successfully (simulated)"));
        } else {
            // Be cautious about revealing *why* it failed (e.g., no contacts vs. system error)
            return ResponseEntity.status(500).body(new MessageResponse("Failed to trigger alert. Ensure contacts are configured."));
        }
    }

    @GetMapping("/contacts")
    @PreAuthorize("isAuthenticated()")
    public List<EmergencyContactDTO> getContacts() {
        Long userId = getCurrentUserId();
        return emergencyService.getContactsForUser(userId);
    }

    @PostMapping("/contacts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmergencyContactDTO> addContact(@RequestBody EmergencyContactDTO contactDTO) {
        Long userId = getCurrentUserId();
        // Basic validation (could use @Valid on DTO)
        if (contactDTO.getName() == null || contactDTO.getName().isBlank() ||
                contactDTO.getPhoneNumber() == null || contactDTO.getPhoneNumber().isBlank()) {
            return ResponseEntity.badRequest().build(); // Consider MessageResponse here too
        }
        EmergencyContactDTO newContact = emergencyService.addContactForUser(userId, contactDTO);
        return ResponseEntity.ok(newContact);
    }

    // Example: Add Delete Endpoint
    @DeleteMapping("/contacts/{contactId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteContact(@PathVariable Long contactId) {
        Long userId = getCurrentUserId();
        try {
            emergencyService.deleteContactForUser(userId, contactId);
            return ResponseEntity.ok(new MessageResponse("Contact deleted successfully."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageResponse("Error: Contact not found."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new MessageResponse("Error: Forbidden."));
        } catch (Exception e) { // Catch broader exceptions if necessary
            return ResponseEntity.status(500).body(new MessageResponse("Error: Could not delete contact."));
        }
    }
}