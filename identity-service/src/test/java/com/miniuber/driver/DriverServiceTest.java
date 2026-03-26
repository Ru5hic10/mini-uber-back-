package com.miniuber.driver;

import com.miniuber.driver.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DriverServiceTest {
    @Autowired
    private DriverService driverService;

    @Test
    void contextLoads() {
        assertNotNull(driverService);
    }
}
