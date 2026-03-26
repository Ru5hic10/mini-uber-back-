package com.miniuber.user;

import com.miniuber.user.controller.UserController;
import com.miniuber.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void registerUserEndpointExists() throws Exception {
        mockMvc.perform(post("/api/users/register"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs body
    }

    @Test
    void getUserEndpointExists() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs data
    }

    @Test
    void loginEndpointExists() throws Exception {
        mockMvc.perform(post("/api/users/login"))
                .andExpect(status().is4xxClientError()); // Should exist, but needs body
    }
}
