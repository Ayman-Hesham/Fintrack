package com.fintrack.fintrack;

import com.fintrack.fintrack.dto.userDTO.AuthResponse;
import com.fintrack.fintrack.dto.userDTO.LoginUserRequest;
import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.dto.userDTO.UserResponse;
import com.fintrack.fintrack.service.JwtService;
import com.fintrack.fintrack.repository.UserRepository;
import com.fintrack.fintrack.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class UserLoginTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void login_Success_ReturnsValidJwtToken() {
        // Arrange - Register a user first
        RegisterUserRequest registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterUserRequest> registerEntity = new HttpEntity<>(registerRequest, headers);
        ResponseEntity<UserResponse> registerResponse = restTemplate.exchange(
                baseUrl + "/api/auth/register",
                HttpMethod.POST,
                registerEntity,
                UserResponse.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        UserResponse responseBody = registerResponse.getBody();
        assertNotNull(responseBody);
        Long userId = responseBody.getId();

        // Act - Login with registered user
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                AuthResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());

        AuthResponse authResponse = loginResponse.getBody();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());
        assertNotNull(authResponse.getUser());
        assertEquals("test@example.com", authResponse.getUser().getEmail());
        assertEquals("Test User", authResponse.getUser().getName());
        assertEquals(userId, authResponse.getUser().getId());

        // Verify JWT token is valid
        User user = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new AssertionError("User should exist"));
        assertTrue(jwtService.isTokenValid(authResponse.getToken(), user));
        assertEquals(userId, jwtService.extractUserId(authResponse.getToken()));
        assertEquals("test@example.com", jwtService.extractEmail(authResponse.getToken()));
    }

    @Test
    void login_InvalidPassword_ReturnsUnauthorized() {
        // Arrange - Register a user
        RegisterUserRequest registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test2@example.com");
        registerRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterUserRequest> registerEntity = new HttpEntity<>(registerRequest, headers);
        restTemplate.exchange(
                baseUrl + "/api/auth/register",
                HttpMethod.POST,
                registerEntity,
                UserResponse.class);

        // Act - Login with wrong password
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test2@example.com");
        loginRequest.setPassword("wrongpassword");

        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }

    @Test
    void login_NonExistentUser_ReturnsUnauthorized() {
        // Arrange
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Act
        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }

    @Test
    void login_InvalidEmailFormat_ReturnsBadRequest() {
        // Arrange
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Act
        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, loginResponse.getStatusCode());
    }

    @Test
    void login_MissingEmail_ReturnsBadRequest() {
        // Arrange
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Act
        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, loginResponse.getStatusCode());
    }

    @Test
    void login_MissingPassword_ReturnsBadRequest() {
        // Arrange
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Act
        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, loginResponse.getStatusCode());
    }

    @Test
    void login_PasswordTooShort_ReturnsBadRequest() {
        // Arrange
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("short"); // Less than 8 characters

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Act
        HttpEntity<LoginUserRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, loginResponse.getStatusCode());
    }
}