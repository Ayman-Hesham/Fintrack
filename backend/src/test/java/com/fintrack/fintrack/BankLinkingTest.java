package com.fintrack.fintrack;

import com.fintrack.fintrack.dto.bankAccountDTO.ConnectBankRequest;
import com.fintrack.fintrack.dto.bankAccountDTO.BankAccountResponse;
import com.fintrack.fintrack.dto.userDTO.AuthResponse;
import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.model.AccountType;
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
class BankLinkingTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String authToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        authToken = registerAndLoginUser();
    }

    private String registerAndLoginUser() {
        String uniqueEmail = "banktest" + System.nanoTime() + "@example.com";
        RegisterUserRequest registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterUserRequest> registerEntity = new HttpEntity<>(registerRequest, headers);
        restTemplate.exchange(
            baseUrl + "/api/auth/register",
            HttpMethod.POST,
            registerEntity,
            String.class
        );

        String loginJson = "{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\"}";
        HttpEntity<String> loginEntity = new HttpEntity<>(loginJson, headers);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.exchange(
            baseUrl + "/api/auth/login",
            HttpMethod.POST,
            loginEntity,
            AuthResponse.class
        );

        return loginResponse.getBody().getToken();
    }

    @Test
    void connectBankAccount_Success_ReturnsBankAccountResponse() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setBankName("Test Bank");
        request.setNickName("My Account");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<BankAccountResponse> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            BankAccountResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Bank", response.getBody().getBankName());
        assertEquals("My Account", response.getBody().getNickName());
        assertTrue(response.getBody().getMaskedAccountNum().contains("****"));
    }

    @Test
    void connectBankAccount_MissingAccountNum_ReturnsBadRequest() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setBankName("Test Bank");
        request.setNickName("My Account");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void connectBankAccount_MissingBankName_ReturnsBadRequest() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setNickName("My Account");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void connectBankAccount_NoAuthentication_ReturnsUnauthorized() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setBankName("Test Bank");
        request.setNickName("My Account");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void connectBankAccount_InvalidToken_ReturnsUnauthorized() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setBankName("Test Bank");
        request.setNickName("My Account");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("invalid_token_xyz");

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void connectBankAccount_MissingNickName_ReturnsBadRequest() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setBankName("Test Bank");
        request.setAccountType(AccountType.SAVINGS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void connectBankAccount_MissingAccountType_ReturnsBadRequest() {
        // Arrange
        ConnectBankRequest request = new ConnectBankRequest();
        request.setAccountNum("12345678");
        request.setBankName("Test Bank");
        request.setNickName("My Account");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<ConnectBankRequest> entity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/banks/connect",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
