package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.LoginRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}