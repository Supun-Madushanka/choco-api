package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.InviteManagerRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InviteManagerResponse;
import com.ceylonechocolate.chocolate_factory_api.service.UserInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserInvitationController {

    private final UserInvitationService userInvitationService;

    @PostMapping("/invite-manager")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT_INVITE_MANAGER')")
    public ResponseEntity<ApiResponse<InviteManagerResponse>> inviteManager(
            @RequestBody InviteManagerRequest request,
            Authentication authentication
    ) {
        InviteManagerResponse response =
                userInvitationService.inviteManager(request, authentication);

        return ResponseEntity.ok(
                ApiResponse.success("Invitation sent successfully", response)
        );
    }
}