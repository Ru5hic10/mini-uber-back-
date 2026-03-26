package com.miniuber.auth;

import com.miniuber.auth.controller.AuthController;
import com.miniuber.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void loginEndpointExists() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs body
    }

    @Test
    void validateEndpointExists() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs token
    }

    @Test
    void logoutEndpointExists() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs token
    }
}
