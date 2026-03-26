package com.miniuber.driver;

import com.miniuber.driver.entity.DriverEarnings;
import com.miniuber.driver.repository.DriverEarningsRepository;
import com.miniuber.driver.service.DriverEarningsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DriverEarningsServiceTest {
    @Mock
    private DriverEarningsRepository driverEarningsRepository;

    @InjectMocks
    private DriverEarningsService driverEarningsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEarningsSummaryReturnsSummaryDto() {
        when(driverEarningsRepository.findByDriverIdAndCreatedAtBetween(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        var summary = driverEarningsService.getEarningsSummary(1L);
        assertNotNull(summary);
        assertEquals(0, summary.getTotalRides());
        assertEquals(0.0, summary.getTotalNetEarnings());
    }

    // Add more tests for other service methods as needed
}
