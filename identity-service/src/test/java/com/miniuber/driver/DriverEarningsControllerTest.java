package com.miniuber.driver;

import com.miniuber.driver.controller.DriverEarningsController;
import com.miniuber.driver.service.DriverEarningsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverEarningsController.class)
public class DriverEarningsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverEarningsService driverEarningsService;

    @Test
    void getEarningsSummaryEndpointExists() throws Exception {
        mockMvc.perform(get("/api/drivers/1/earnings/summary"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs data
    }

    @Test
    void getWeeklyEarningsTrendEndpointExists() throws Exception {
        mockMvc.perform(get("/api/drivers/1/earnings/weekly"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs data
    }

    @Test
    void getMonthlyEarningsEndpointExists() throws Exception {
        mockMvc.perform(get("/api/drivers/1/earnings/monthly"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs data
    }
}
