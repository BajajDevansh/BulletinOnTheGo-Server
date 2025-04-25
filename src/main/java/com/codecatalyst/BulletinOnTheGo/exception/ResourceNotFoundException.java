package com.codecatalyst.BulletinOnTheGo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for scenarios where a requested resource is not found.
 * Maps to HTTP 404 Not Found status.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND) // Sets the default HTTP status if uncaught
public class ResourceNotFoundException extends RuntimeException { // Make it a RuntimeException

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
