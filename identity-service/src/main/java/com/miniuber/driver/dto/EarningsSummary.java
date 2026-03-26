package com.miniuber.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarningsSummary {
    private int totalRides;
    private double totalGrossEarnings;
    private double totalCommission;
    private double totalNetEarnings;
    private double averagePerRide;
    private String date;
}
