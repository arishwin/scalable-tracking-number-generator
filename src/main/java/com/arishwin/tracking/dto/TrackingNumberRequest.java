package com.arishwin.tracking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Request DTO for the tracking number generation API.
 * Contains all the query parameters specified in the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumberRequest {
    @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country ID must be a valid ISO 3166-1 alpha-2 code (e.g., 'MY')")
    @Size(min = 2, max = 2, message = "Origin country ID must be exactly 2 characters")
    private String originCountryId;

    @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country ID must be a valid ISO 3166-1 alpha-2 code (e.g., 'ID')")
    @Size(min = 2, max = 2, message = "Destination country ID must be exactly 2 characters")
    private String destinationCountryId;

    @DecimalMin(value = "0.001", message = "Weight must be greater than 0")
    @Digits(integer = 10, fraction = 3, message = "Weight must have at most 3 decimal places")
    private BigDecimal weight;

    private OffsetDateTime createdAt;

    private UUID customerId;

    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;

    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Customer slug must be in kebab-case format (e.g., 'redbox-logistics')")
    @Size(max = 255, message = "Customer slug must not exceed 255 characters")
    private String customerSlug;
}
