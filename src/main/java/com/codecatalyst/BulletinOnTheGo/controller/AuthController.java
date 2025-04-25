package com.codecatalyst.BulletinOnTheGo.controller;

import com.codecatalyst.BulletinOnTheGo.dto.auth.JwtResponse;
import com.codecatalyst.BulletinOnTheGo.dto.auth.LoginRequest;
import com.codecatalyst.BulletinOnTheGo.dto.auth.SignupRequest;
import com.codecatalyst.BulletinOnTheGo.dto.message.MessageResponse;
import com.codecatalyst.BulletinOnTheGo.entity.User;
import com.codecatalyst.BulletinOnTheGo.repositories.UserRepository;
import com.codecatalyst.BulletinOnTheGo.security.UserDetailsImpl;
import com.codecatalyst.BulletinOnTheGo.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "*", maxAge = 3600) // Consider making this more specific or using global config
@RestController
@RequestMapping("/api/auth")

public class AuthController {

    // Use final fields with @RequiredArgsConstructor
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Renamed from 'encoder' for clarity
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,UserRepository userRepository,PasswordEncoder passwordEncoder,JwtUtils jwtUtils){
        this.authenticationManager=authenticationManager;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtils=jwtUtils;
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authentication logic remains the same internally (uses provider configured with NoOpEncoder)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Pass String ID to JwtResponse constructor
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(), // This now returns String
                userDetails.getUsername(),
                userDetails.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account - STILL USING PLAIN TEXT PASSWORD based on your last snippet
        // REMEMBER TO RE-ADD HASHING FOR PRODUCTION
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                // WARNING: Storing plain text password - should use hashing in production
                // passwordEncoder.encode(signUpRequest.getPassword()) // <-- Hashed version
                signUpRequest.getPassword() // <-- Plain text version (matching NoOpPasswordEncoder)
        );

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
