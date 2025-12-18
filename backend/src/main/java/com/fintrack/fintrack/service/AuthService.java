package com.fintrack.fintrack.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.lang.NonNull;
import com.fintrack.fintrack.repository.UserRepository;
import com.fintrack.fintrack.dto.userDTO.AuthResponse;
import com.fintrack.fintrack.dto.userDTO.LoginUserRequest;
import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.dto.userDTO.UserResponse;
import com.fintrack.fintrack.mapper.UserMapper;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.security.CustomUserPrincipal;
import com.fintrack.fintrack.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UserResponse createUser(RegisterUserRequest dto) {
        User user = userMapper.toEntity(dto);
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User newUser = userRepository.save(user);
        return userMapper.toUserResponse(newUser);
    }

    public AuthResponse loginUser(LoginUserRequest dto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        Authentication authenticated = authenticationManager.authenticate(authentication);

        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authenticated.getPrincipal();
        User user = userPrincipal.getUser();

        String token = jwtService.generateToken(user);

        return userMapper.toAuthResponse(user, token);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getUserById(@NonNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
