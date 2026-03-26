package com.miniuber.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarningsHistoryItem {
    private Long id;
    private Long rideId;
    private Double grossAmount;
    private Double commissionAmount;
    private Double netAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
