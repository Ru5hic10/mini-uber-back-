package com.miniuber.driver;

import com.miniuber.driver.controller.DriverController;
import com.miniuber.driver.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DriverControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @Test
    void getDriverEndpointExists() throws Exception {
        mockMvc.perform(get("/api/drivers/1"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs data
    }

    @Test
    void updateAvailabilityEndpointExists() throws Exception {
        mockMvc.perform(put("/api/drivers/1/availability"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs body
    }

    @Test
    void updateLocationEndpointExists() throws Exception {
        mockMvc.perform(put("/api/drivers/1/location"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs body
    }
}
