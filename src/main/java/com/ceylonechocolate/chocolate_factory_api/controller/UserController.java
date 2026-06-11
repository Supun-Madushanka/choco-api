package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AcceptInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.SendInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InvitationResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;
import com.ceylonechocolate.chocolate_factory_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Invitation Endpoints
    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @Valid @RequestBody SendInvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        InvitationResponse response = userService.sendInvitation(
                request,
                userDetails.getUsername()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Invitation sent successfully", response)
        );
    }

    @GetMapping("/invite/validate")
    public ResponseEntity<ApiResponse<InvitationResponse>> validateToken(
            @RequestParam String token) {

        InvitationResponse response = userService
                .validateInvitationToken(token);

        return ResponseEntity.ok(
                ApiResponse.success("Token is valid", response)
        );
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @Valid @RequestBody AcceptInvitationRequest request) {

        userService.acceptInvitation(request);

        return ResponseEntity.ok(
                ApiResponse.success("Registration completed successfully")
        );
    }

    // User Management Endpoints
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", users)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", user)
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success("User deactivated successfully")
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @PathVariable Long id) {

        userService.activateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success("User activated successfully")
        );
    }

    @PutMapping("/{id}/change-role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(
            @PathVariable Long id,
            @RequestParam Long roleId) {

        userService.changeUserRole(id, roleId);

        return ResponseEntity.ok(
                ApiResponse.success("User role changed successfully")
        );
    }
}