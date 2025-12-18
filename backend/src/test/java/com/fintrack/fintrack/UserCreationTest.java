package com.fintrack.fintrack;

import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.dto.userDTO.UserResponse;
import com.fintrack.fintrack.repository.UserRepository;
import com.fintrack.fintrack.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserCreationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_Success_CreatesUserInDatabase() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        // Act
        UserResponse response = authService.createUser(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());

        // Verify user exists in database
        assertTrue(userRepository.existsByEmail("john@example.com"));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        RegisterUserRequest request1 = new RegisterUserRequest();
        request1.setName("John Doe");
        request1.setEmail("john@example.com");
        request1.setPassword("password123");

        RegisterUserRequest request2 = new RegisterUserRequest();
        request2.setName("Jane Doe");
        request2.setEmail("john@example.com"); // Same email
        request2.setPassword("password456");

        // Act
        authService.createUser(request1);

        // Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.createUser(request2)
        );

        assertEquals("Email already exists", exception.getMessage());
    }
}