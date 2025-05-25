package com.arishwin.tracking.controller;

import com.arishwin.tracking.dto.TrackingNumberRequest;
import com.arishwin.tracking.dto.TrackingNumberResponse;
import com.arishwin.tracking.service.TrackingNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * REST controller for the tracking number generation API.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;
    private final com.arishwin.tracking.validator.RequestValidator requestValidator;

    /**
     * Endpoint to generate a unique tracking number.
     * 
     * @param originCountryId The order's origin country code in ISO 3166-1 alpha-2 format
     * @param destinationCountryId The order's destination country code in ISO 3166-1 alpha-2 format
     * @param weight The order's weight in kilograms
     * @param createdAt The order's creation timestamp in RFC 3339 format
     * @param customerId The customer's UUID
     * @param customerName The customer's name
     * @param customerSlug The customer's name in slug-case/kebab-case
     * @return A response containing the generated tracking number and creation timestamp
     */
    @GetMapping("/next-tracking-number")
    public TrackingNumberResponse getNextTrackingNumber(
            @RequestParam(name = "origin_country_id", required = false) String originCountryId,
            @RequestParam(name = "destination_country_id", required = false) String destinationCountryId,
            @RequestParam(name = "weight", required = false) BigDecimal weight,
            @RequestParam(name = "created_at", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdAt,
            @RequestParam(name = "customer_id", required = false) UUID customerId,
            @RequestParam(name = "customer_name", required = false) String customerName,
            @RequestParam(name = "customer_slug", required = false) String customerSlug) {

        log.debug("Received request: origin={}, destination={}, weight={}, createdAt={}, customerId={}, customerName={}, customerSlug={}",
                originCountryId, destinationCountryId, weight, createdAt, customerId, customerName, customerSlug);

        // 1. build request object
        TrackingNumberRequest request = TrackingNumberRequest.builder()
                .originCountryId(originCountryId)
                .destinationCountryId(destinationCountryId)
                .weight(weight)
                .createdAt(createdAt)
                .customerId(customerId)
                .customerName(customerName)
                .customerSlug(customerSlug)
                .build();

        // 2. validate request parameters
        requestValidator.validate(request);

        // 3. generate tracking number and return response
        return trackingNumberService.generateTrackingNumber(request);
    }
}
