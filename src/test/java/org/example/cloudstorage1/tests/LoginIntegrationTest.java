package org.example.cloudstorage1.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cloudstorage1.BaseIntegrationTest;
import org.example.cloudstorage1.dto.LoginRequest;
import org.example.cloudstorage1.dto.SignupRequest;
import org.example.cloudstorage1.repository.UserRepository;
import org.example.cloudstorage1.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class LoginIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        userService.signUp(new SignupRequest("test", "test"));
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test", "test");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest("test", "wrongpassword");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectNonExistentUser() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent", "test");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAccessProtectedEndpointAfterLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test", "test");

        // 1. Логинимся и получаем SESSION cookie
        MvcResult loginResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // 2. Извлекаем SESSION cookie из ответа
        Cookie sessionCookie = loginResult.getResponse().getCookie("SESSION");

        // 3. Используем SESSION cookie для доступа к защищённому эндпоинту
        mockMvc.perform(get("/api/user/me")
                        .cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void shouldRejectAccessWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectBlankUsername() throws Exception {
        LoginRequest request = new LoginRequest("", "test");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNullUsername() throws Exception {
        LoginRequest request = new LoginRequest(null, "test");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        LoginRequest request = new LoginRequest("test", "");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNullPassword() throws Exception {
        LoginRequest request = new LoginRequest("test", null);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}