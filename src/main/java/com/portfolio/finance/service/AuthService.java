package com.portfolio.finance.service;

import com.portfolio.finance.config.JwtUtil;
import com.portfolio.finance.dto.AuthResponse;
import com.portfolio.finance.dto.LoginRequest;
import com.portfolio.finance.dto.RegisterRequest;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.exception.InvalidCredentialsException;
import com.portfolio.finance.exception.ResourceAlreadyExistsException;
import com.portfolio.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException | AuthenticationServiceException exception) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        return issueToken(user);
    }

    private AuthResponse issueToken(User user) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationSeconds())
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
