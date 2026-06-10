package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String roleDisplayName;
    private String accessToken;
    private String refreshToken;
}