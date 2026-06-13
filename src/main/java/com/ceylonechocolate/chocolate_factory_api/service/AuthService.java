package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.*;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AuthResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    UserResponse getMe(String email);
    void changePassword(String email, ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
}