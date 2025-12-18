package com.fintrack.fintrack.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.fintrack.fintrack.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final Long expirationMinutes;

    public JwtService(SecretKey signingKey, com.fintrack.fintrack.config.JwtConfig jwtConfig) {
        this.signingKey = signingKey;
        this.expirationMinutes = jwtConfig.getExpirationMinutes();
    }

    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user ID cannot be null");
        }
        
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        
        return Jwts.builder()
            .claim("sub", String.valueOf(user.getId()))
            .claim("email", user.getEmail()) 
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(signingKey)
            .compact();
    }

    public boolean isTokenValid(String token, User user) {
        if (token == null || user == null || user.getEmail() == null) {
            return false;
        }
        try {
            String subject = extractClaim(token, Claims::getSubject);
            return subject.equals(String.valueOf(user.getId())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        if (token == null || resolver == null) {
            throw new IllegalArgumentException("Token and resolver cannot be null");
        }
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    public String extractEmail(String token) {
        try {
            return extractClaim(token, claims -> claims.get("email", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}