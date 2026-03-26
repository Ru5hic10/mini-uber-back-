package com.miniuber.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarningsTrendItem {
    private String date;
    private int totalRides;
    private double totalEarnings;
    private double averagePerRide;
}
