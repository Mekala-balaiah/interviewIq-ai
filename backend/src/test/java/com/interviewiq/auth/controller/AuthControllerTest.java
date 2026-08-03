package com.interviewiq.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.dto.LoginRequest;
import com.interviewiq.auth.dto.RegisterRequest;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.enums.UserStatus;
import com.interviewiq.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Assuming you have an application-test.yml or default to H2
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.CANDIDATE);

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.email").value("john.doe@example.com"));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnConflict() throws Exception {
        // Arrange
        User existingUser = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("existing@example.com")
                .passwordHash("hashed")
                .role(UserRole.CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.CANDIDATE);

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("login@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(UserRole.CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("login@example.com");
        request.setPassword("password123");

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("login2@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(UserRole.CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("login2@example.com");
        request.setPassword("wrongpassword");

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isUnauthorized()); // AuthenticationManager throws BadCredentialsException
    }
}
