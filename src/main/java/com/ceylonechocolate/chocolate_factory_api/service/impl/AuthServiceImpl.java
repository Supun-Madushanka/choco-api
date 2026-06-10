package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.LoginRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AuthResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.security.JwtUtil;
import com.ceylonechocolate.chocolate_factory_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {

        // Step 1 — Authenticate email and password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Step 2 — Load user from database
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("User not found with email: " + request.getEmail())
                );

        // Step 3 — Check if user is active
        if (!user.getIsActive()) {
            throw new BadCredentialsException(
                    "Your account has been deactivated. " +
                            "Please contact HR."
            );
        }

        // Step 4 — Generate tokens
        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName(),
                user.getId()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getEmail()
        );

        // Step 5 — Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Step 6 — Build and return response
        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .roleDisplayName(user.getRole().getDisplayName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}