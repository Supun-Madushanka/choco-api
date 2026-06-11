package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private String role;
    private String roleDisplayName;
    private String roleLevel;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}