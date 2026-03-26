package com.miniuber.driver.service;

import com.miniuber.user.service.UserService;

import com.miniuber.driver.dto.DriverAvailabilityRequest;
import com.miniuber.driver.dto.DriverLocationUpdateRequest;
import com.miniuber.driver.dto.DriverRegistrationRequest;
import com.miniuber.driver.dto.DriverResponse;
import com.miniuber.driver.entity.Driver;
import com.miniuber.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverEarningsService driverEarningsService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Example helper: fetch user info for a driver (if needed in future logic)
    public com.miniuber.user.entity.User getUserInfoForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        // Example: assume driver's email matches a user
        return userService.getUserByEmail(driver.getEmail());
    }

    @Transactional
    public DriverResponse registerDriver(DriverRegistrationRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already registered");
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setPhone(request.getPhone());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleType(request.getVehicleType());
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleModel(request.getVehicleModel());
        driver.setAvailable(false);
        driver.setVerified(false);

        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }

    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return mapToResponse(driver);
    }

    @Transactional
    public DriverResponse updateAvailability(Long driverId, DriverAvailabilityRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setAvailable(request.getAvailable());
        // Automatically mark driver verified when they explicitly go online to avoid being filtered out
        if (Boolean.TRUE.equals(request.getAvailable())) {
            driver.setVerified(true);
        }
        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    @Transactional
    public DriverResponse updateLocation(Long driverId, DriverLocationUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());
        
        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    @Transactional
    public DriverResponse updateRating(Long driverId, Double newRating) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setRating(newRating);
        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    public List<DriverResponse> getAvailableDrivers() {
        return driverRepository.findAllAvailableDrivers()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Driver getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public com.miniuber.driver.dto.EarningsSummary getEarningsSummary(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        try {
            var earningsSummary = driverEarningsService.getEarningsSummary(driverId);
            double totalEarnings = earningsSummary.getTotalNetEarnings();
            int totalRides = earningsSummary.getTotalRides();
            double averagePerRide = totalRides > 0 ? earningsSummary.getAveragePerRide() : 0.0;

            return new com.miniuber.driver.dto.EarningsSummary(
                    totalRides,
                    earningsSummary.getTotalGrossEarnings(),
                    earningsSummary.getTotalCommission(),
                    totalEarnings,
                    averagePerRide,
                    earningsSummary.getDate()
            );
        } catch (Exception e) {
            // Return zero data if ride-service is unavailable
            return new com.miniuber.driver.dto.EarningsSummary(
                    0, 0.0, 0.0, 0.0, 0.0, ""
            );
        }
    }

    public List<com.miniuber.driver.dto.EarningsTrendItem> getWeeklyEarningsTrend(Long driverId) {
        List<com.miniuber.driver.dto.EarningsTrendItem> weeklyTrend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try {
            List<com.miniuber.driver.dto.EarningsTrendItem> serviceTrend = driverEarningsService.getWeeklyEarningsTrend(driverId);
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                double dailyEarnings = serviceTrend.stream()
                        .filter(day -> day.getDate().equals(date.toString()))
                        .mapToDouble(day -> day.getTotalEarnings())
                        .findFirst().orElse(0.0);

                weeklyTrend.add(new com.miniuber.driver.dto.EarningsTrendItem(
                        date.toString(), 0, dailyEarnings, dailyEarnings
                ));
            }
        } catch (Exception e) {
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                weeklyTrend.add(new com.miniuber.driver.dto.EarningsTrendItem(
                        date.toString(), 0, 0.0, 0.0
                ));
            }
        }

        return weeklyTrend;
    }

    public com.miniuber.driver.dto.MonthlyEarnings getMonthlyEarnings(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        YearMonth currentMonth = YearMonth.now();
        
        try {
            var monthlySummary = driverEarningsService.getMonthlyEarnings(driverId);
            return new com.miniuber.driver.dto.MonthlyEarnings(
                    currentMonth.toString(),
                    monthlySummary.getTotalRides(),
                    monthlySummary.getTotalGrossEarnings(),
                    monthlySummary.getTotalCommission(),
                    monthlySummary.getTotalNetEarnings(),
                    monthlySummary.getAveragePerRide(),
                    monthlySummary.getBestDay(),
                    monthlySummary.getBestDayEarnings()
            );
        } catch (Exception e) {
            return new com.miniuber.driver.dto.MonthlyEarnings(
                    currentMonth.toString(), 0, 0.0, 0.0, 0.0, 0.0, null, null
            );
        }
    }

    private DriverResponse mapToResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getVehicleType(),
                driver.getVehicleNumber(),
                driver.getVehicleModel(),
                driver.getAvailable(),
                driver.getVerified(),
                driver.getRating(),
                driver.getTotalRides(),
                driver.getCreatedAt()
        );
    }
}
