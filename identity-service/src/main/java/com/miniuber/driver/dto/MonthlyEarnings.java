package com.miniuber.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyEarnings {
    private String month;
    private int totalRides;
    private double totalGrossEarnings;
    private double totalCommission;
    private double totalNetEarnings;
    private double averagePerRide;
    private String bestDay;
    private Double bestDayEarnings;
}
