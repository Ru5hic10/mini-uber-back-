package com.miniuber.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideOffer {
    private Long rideId;
    private Long driverId;
    private Long riderId;
    private String pickupLocation;
    private String dropoffLocation;
    private Double estimatedFare;
    private Double distance;
    private Long offerExpiresAt;
}
