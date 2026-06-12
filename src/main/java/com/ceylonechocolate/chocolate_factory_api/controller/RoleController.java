package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RoleResponse;
import com.ceylonechocolate.chocolate_factory_api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {

        List<RoleResponse> roles = roleService.getAllRoles();

        return ResponseEntity.ok(
                ApiResponse.success("Roles fetched successfully", roles)
        );
    }

    @GetMapping("/by-level")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getRolesByLevel(
            @RequestParam String level) {

        List<RoleResponse> roles = roleService.getRolesByLevel(level);

        return ResponseEntity.ok(
                ApiResponse.success("Roles fetched successfully", roles)
        );
    }
}