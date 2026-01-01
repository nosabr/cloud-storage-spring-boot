package org.example.cloudstorage1.tests;

import org.example.cloudstorage1.BaseIntegrationTest;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.dto.ErrorResponse;
import org.example.cloudstorage1.dto.LoginRequest;
import org.example.cloudstorage1.dto.SignupRequest;
import org.example.cloudstorage1.dto.UserResponse;
import org.example.cloudstorage1.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class LoginIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void createUser(){
        userRepository.deleteAll();
        userService.signUp(new SignupRequest("test", "test"));
    }

    @Test
    void shouldLogin() {
        LoginRequest request = new LoginRequest("test","test");
        ResponseEntity<UserResponse> response = testRestTemplate.
                postForEntity("/api/auth/sign-in",
                        request,
                        UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody() != null ? response.getBody().username() : null).isEqualTo("test");
        assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();
    }

    @Test
    void shouldRejectLogin() {
        LoginRequest request = new LoginRequest("test1","test");
        ResponseEntity<UserResponse> response = testRestTemplate.
                postForEntity("/api/auth/sign-in",
                        request,
                        UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectAccessWithoutSession() {
        ResponseEntity<UserResponse> response = testRestTemplate.
                getForEntity("/api/user/me",
                        UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectLoginWithWrongPassword() {
        LoginRequest request = new LoginRequest("test", "wrongpassword");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-in",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectLoginWithEmptyUsername() {
        LoginRequest request = new LoginRequest("", "test");
        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-in",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectLoginWithEmptyPassword() {
        LoginRequest request = new LoginRequest("test", "");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-in",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldAccessProtectedEndpointAfterLogin() {
        // 1. Логинимся
        LoginRequest loginRequest = new LoginRequest("test", "test");
        ResponseEntity<UserResponse> loginResponse = testRestTemplate.postForEntity(
                "/api/auth/sign-in",
                loginRequest,
                UserResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 2. Извлекаем cookie
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();

        Assertions.assertNotNull(cookies);
        String sessionCookie = cookies.stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("JSESSIONID not found"));

        // 3. Создаём headers с cookie
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 4. Делаем запрос на защищённый эндпоинт
        ResponseEntity<UserResponse> meResponse = testRestTemplate.exchange(
                "/api/user/me",
                HttpMethod.GET,
                requestEntity,
                UserResponse.class
        );

        // 5. Проверяем результат
        assertThat(meResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse user = Objects.requireNonNull(meResponse.getBody());
        assertThat(user.username()).isEqualTo("test");
    }

}
