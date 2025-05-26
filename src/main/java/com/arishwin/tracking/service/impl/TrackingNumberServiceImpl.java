package com.arishwin.tracking.service.impl;

import com.arishwin.tracking.dto.TrackingNumberRequest;
import com.arishwin.tracking.dto.TrackingNumberResponse;
import com.arishwin.tracking.manager.WorkerIdManager;
import com.arishwin.tracking.service.TrackingNumberService;
import com.arishwin.tracking.util.Base36Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the TrackingNumberService interface.
 * Generates unique tracking numbers that match the regex pattern: ^[A-Z0-9]{1,16}$
 * Uses Redis to ensure uniqueness across multiple instances.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingNumberServiceImpl implements TrackingNumberService {


    private static final int TRACKING_NUMBER_LENGTH = 16;
    private static final String SEQUENCE_KEY_PREFIX = "seq:";

    @Value("${tracking.epoch}")
    private long epoch;

    private final RedisTemplate<String, String> redisTemplate;

    private final WorkerIdManager workerIdManager;

    @Override
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request) {

        // 1. generate unique tracking number
        String trackingNumber = generateUniqueTrackingNumber(request);

        // 2. add created time for response, use current time if request doesn't specify
        OffsetDateTime createdAt = request.getCreatedAt() != null
                ? request.getCreatedAt()
                : OffsetDateTime.now();

        // 3. build response and return
        return TrackingNumberResponse.builder()
                .trackingNumber(trackingNumber)
                .createdAt(createdAt)
                .build();
    }


    private String generateSnowflakeId() {
        long nowMs = Instant.now().toEpochMilli();
        long timestamp = nowMs - epoch;

        // increment key for every milisecond, ensuring uniqueness
        String key = SEQUENCE_KEY_PREFIX + nowMs;
        long seq = redisTemplate.opsForValue().increment(key);

        // set TTL so old keys expire
        if (seq == 1) {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        }

        // snowflike id bit format:
        // [timestamp(42) | workerId(10) | seq(8)]
        long id = (timestamp << (10 + 8))
                | ((workerIdManager.getWorkerId() & 0x3FF) << 8)
                | (seq & 0xFFF);

        // convert to base 36 string
        return Base36Encoder.encode(id);
    }

    private String generateUniqueTrackingNumber(TrackingNumberRequest request) {
        // 1. generate the Snowflake-like ID
        String snowflakeId = generateSnowflakeId();

        // 2. combine with country codes
        return buildTrackingNumber(request, snowflakeId);
    }

    private String buildTrackingNumber(TrackingNumberRequest request, String snowflakeId) {
        StringBuilder sb = new StringBuilder();

        // 1. add origin country code (first 2 chars)
        if (request.getOriginCountryId() != null && !request.getOriginCountryId().isEmpty()) {
            sb.append(request.getOriginCountryId().toUpperCase());
        }

        // 2. add destination country code (first 2 chars)
        if (request.getDestinationCountryId() != null && !request.getDestinationCountryId().isEmpty()) {
            sb.append(request.getDestinationCountryId().toUpperCase());
        }

        // 3. Add the Snowflake ID
        sb.append(snowflakeId);

        // 4. ensure the tracking number doesn't exceed the maximum length
        return sb.substring(0, Math.min(sb.length(), TRACKING_NUMBER_LENGTH));
    }
}
