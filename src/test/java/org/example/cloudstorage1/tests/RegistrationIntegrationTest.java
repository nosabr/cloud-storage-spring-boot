package org.example.cloudstorage1.tests;

import org.example.cloudstorage1.BaseIntegrationTest;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.dto.ErrorResponse;
import org.example.cloudstorage1.dto.SignupRequest;
import org.example.cloudstorage1.dto.UserResponse;
import org.example.cloudstorage1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RegistrationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUser() {
        SignupRequest request = new SignupRequest("test", "test123");
        ResponseEntity<UserResponse> response = testRestTemplate.
                postForEntity("/api/auth/sign-up",
                        request,
                        UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody() != null ? response.getBody().username() : null).isEqualTo("test");
    }

    @Test
    void shouldRejectDuplicateUsername() {
        userService.signUp(new SignupRequest("test", "test"));
        SignupRequest request = new SignupRequest("test", "test");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldRejectBlankUsername() {
        SignupRequest request = new SignupRequest("", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectNullUsername() {
        SignupRequest request = new SignupRequest(null, "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectTooShortUsername() {
        SignupRequest request = new SignupRequest("abc", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectTooLongUsername() {
        SignupRequest request = new SignupRequest("verylongusername123", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectUsernameWithSpecialCharacters() {
        SignupRequest request = new SignupRequest("user@123", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectUsernameWithSpaces() {
        SignupRequest request = new SignupRequest("user name", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectUsernameWithCyrillic() {
        SignupRequest request = new SignupRequest("юзер123", "password123");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // === Password validation tests ===

    @Test
    void shouldRejectBlankPassword() {
        SignupRequest request = new SignupRequest("testuser", "");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectNullPassword() {
        SignupRequest request = new SignupRequest("testuser", null);

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectTooShortPassword() {
        SignupRequest request = new SignupRequest("testuser", "abc");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectTooLongPassword() {
        SignupRequest request = new SignupRequest("testuser", "verylongpasswordthatexceeds20characters");

        ResponseEntity<ErrorResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // === Valid cases ===

    @Test
    void shouldAcceptValidUsernameWithLetters() {
        SignupRequest request = new SignupRequest("testuser", "password123");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptValidUsernameWithNumbers() {
        SignupRequest request = new SignupRequest("user1234", "password123");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptValidUsernameWithUnderscore() {
        SignupRequest request = new SignupRequest("test_user", "password123");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptMinimumLengthUsername() {
        SignupRequest request = new SignupRequest("test", "password123");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptMaximumLengthUsername() {
        SignupRequest request = new SignupRequest("testuser1234", "password123");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptMinimumLengthPassword() {
        SignupRequest request = new SignupRequest("testuser", "pass");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAcceptMaximumLengthPassword() {
        SignupRequest request = new SignupRequest("testuser", "12345678901234567890");

        ResponseEntity<UserResponse> response = testRestTemplate.postForEntity(
                "/api/auth/sign-up",
                request,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


}
