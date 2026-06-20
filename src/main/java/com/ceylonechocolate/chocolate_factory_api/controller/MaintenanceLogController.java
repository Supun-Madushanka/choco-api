package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MaintenanceLogRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MaintenanceLogResponse;
import com.ceylonechocolate.chocolate_factory_api.service.MaintenanceLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance-logs")
@RequiredArgsConstructor
public class MaintenanceLogController {

    private final MaintenanceLogService maintenanceLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<MaintenanceLogResponse>>>
    getAllLogs() {
        return ResponseEntity.ok(
                ApiResponse.success("Maintenance logs fetched successfully",
                        maintenanceLogService.getAllLogs())
        );
    }

    @GetMapping("/machine/{machineId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<MaintenanceLogResponse>>>
    getLogsByMachine(@PathVariable Long machineId) {
        return ResponseEntity.ok(
                ApiResponse.success("Maintenance logs fetched successfully",
                        maintenanceLogService.getLogsByMachine(machineId))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<MaintenanceLogResponse>> createLog(
            @Valid @RequestBody MaintenanceLogRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Maintenance log created successfully",
                        maintenanceLogService.createLog(
                                request, userDetails.getUsername()))
        );
    }
}