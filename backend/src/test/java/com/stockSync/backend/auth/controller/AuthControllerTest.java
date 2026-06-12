package com.stockSync.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockSync.backend.auth.dto.AuthResponse;
import com.stockSync.backend.auth.dto.LoginRequest;
import com.stockSync.backend.auth.dto.RegisterRequest;
import com.stockSync.backend.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void givenValidCredentials_whenLogin_thenReturnTokenAndUserInfo() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();
        AuthResponse response = AuthResponse.builder()
                .token("mock-token")
                .email("test@test.com")
                .nombre("Test")
                .role("ADMIN")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        // Verify
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    public void givenValidRequest_whenRegister_thenCreateUserAndReturnSuccess() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test Company", "Test User", "test@test.com", "Password123!");

        // Act & Assert
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado con éxito. Por favor, inicia sesión."));

        // Verify
        verify(authService).register(any(RegisterRequest.class));
    }
}
