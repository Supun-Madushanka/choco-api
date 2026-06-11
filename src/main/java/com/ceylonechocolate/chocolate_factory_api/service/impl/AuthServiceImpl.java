package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ChangePasswordRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.LoginRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.RefreshTokenRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AuthResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.TokenBlacklist;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.TokenBlacklistRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.security.JwtUtil;
import com.ceylonechocolate.chocolate_factory_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {

        // Load user from database
        User user = userRepository
                .findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("User not found")
                );

        // Authenticate email and password
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

        // Check if user is active
        if (!user.getIsActive()) {
            throw new BadCredentialsException(
                    "Your account has been deactivated. Please contact support."
            );
        }

        // Generate tokens
        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName(),
                user.getId()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getEmail()
        );

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Build and return response
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

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // Extract email from refresh token
        String email;
        try {
            email = jwtUtil.extractEmail(request.getRefreshToken());
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Load user from database
        User user = userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid refresh token")
                );

        // Check if user is still active
        if (!user.getIsActive()) {
            throw new BadCredentialsException(
                    "Your account has been deactivated. Please contact support."
            );
        }

        // Validate refresh token
        if (!jwtUtil.isTokenValid(request.getRefreshToken(), user.getEmail())) {
            throw new BadCredentialsException("Refresh token is expired or invalid");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName(),
                user.getId()
        );

        String newRefreshToken = jwtUtil.generateRefreshToken(
                user.getEmail()
        );

        // Return response
        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .roleDisplayName(user.getRole().getDisplayName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String token) {

        // Step 1 — Validate token
        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token");
        }

        // Step 2 — Check if already blacklisted
        if (tokenBlacklistRepository.existsByToken(token)) {
            throw new BadCredentialsException("Token already invalidated");
        }

        // Step 3 — Get token expiry
        Date expiryDate;
        try {
            expiryDate = jwtUtil.extractExpiration(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token");
        }

        // Step 4 — Add token to blacklist
        TokenBlacklist blacklistedToken = TokenBlacklist.builder()
                .token(token)
                .email(email)
                .expiresAt(expiryDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();

        tokenBlacklistRepository.save(blacklistedToken);
    }

    @Override
    public UserResponse getMe(String email) {

        User user = userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new BadCredentialsException("User not found")
                );

        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .role(user.getRole().getName())
                .roleDisplayName(user.getRole().getDisplayName())
                .roleLevel(user.getRole().getLevel().name())
                .isActive(user.getIsActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        // Load user
        User user = userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new BadCredentialsException("User not found")
                );

        // Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        // Check new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        // Check new password is different from current
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        // Update password
        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );
        userRepository.save(user);
    }


}