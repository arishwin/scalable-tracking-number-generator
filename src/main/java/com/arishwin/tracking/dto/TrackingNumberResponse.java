package com.arishwin.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumberResponse {

    private String trackingNumber;

    private OffsetDateTime createdAt;
}