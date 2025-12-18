package com.fintrack.fintrack.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import javax.crypto.SecretKey;

@Configuration
@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {
    @NotBlank
    private String secret;
    @NotNull
    private Long expirationMinutes;

    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}