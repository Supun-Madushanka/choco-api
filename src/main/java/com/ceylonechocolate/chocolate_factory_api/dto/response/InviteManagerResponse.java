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
public class InviteManagerResponse {

    private Long invitationId;
    private String email;
    private String role;
    private String status;
    private LocalDateTime expiresAt;
}