package com.portfolio.finance.service;

import com.portfolio.finance.dto.AuthResponse;
import com.portfolio.finance.dto.LoginRequest;
import com.portfolio.finance.dto.RegisterRequest;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.exception.InvalidCredentialsException;
import com.portfolio.finance.exception.ResourceAlreadyExistsException;
import com.portfolio.finance.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long TOKEN_EXPIRATION_SECONDS = 3600L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new ResourceAlreadyExistsException(
                            "User already exists with email: " + request.getEmail());
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        return issueToken(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueToken(user);
    }

    private AuthResponse issueToken(User user) {
        return AuthResponse.builder()
                .accessToken(UUID.randomUUID().toString())
                .tokenType("Bearer")
                .expiresIn(TOKEN_EXPIRATION_SECONDS)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
