package com.arishwin.tracking.service;

import com.arishwin.tracking.dto.TrackingNumberRequest;
import com.arishwin.tracking.dto.TrackingNumberResponse;

public interface TrackingNumberService {

    TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request);
}