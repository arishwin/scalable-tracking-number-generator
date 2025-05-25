package com.arishwin.tracking.validator;

import com.arishwin.tracking.dto.TrackingNumberRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator for request parameters.
 * Validates TrackingNumberRequest objects against their validation constraints.
 */
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public void validate(TrackingNumberRequest request) {
        Set<ConstraintViolation<TrackingNumberRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation error: " + errorMessage);
        }
    }
}