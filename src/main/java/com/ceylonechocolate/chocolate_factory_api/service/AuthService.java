package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ChangePasswordRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.LoginRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.RefreshTokenRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AuthResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    UserResponse getMe(String email);
    void changePassword(String email, ChangePasswordRequest request);
}