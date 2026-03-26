package com.miniuber.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.admin.auto-create=false",
    "stripe.api.key=sk_test_51RMlop4MEIRh82GFzLhHOmgef3zdFhpne6vJ7bmFbdhKRT6oHvEoY9pMB5gs3BwVRTeq2fLc8GXwy08Ny1W9vtPN00BLrxLof4",
    "stripe.api.version=2023-10-16",
    "stripe.webhook.secret=whsec_test_51RMlop4MEIRh82GF"
})
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
}
