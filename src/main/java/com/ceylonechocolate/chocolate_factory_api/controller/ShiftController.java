package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftUpdateRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAllShifts() {
        return ResponseEntity.ok(
                ApiResponse.success("Shifts fetched successfully",
                        shiftService.getAllShifts())
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER'," +
            "'PRODUCTION_MANAGER','PRODUCTION_SUPERVISOR'," +
            "'WAREHOUSE_MANAGER','WAREHOUSE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getActiveShifts() {
        return ResponseEntity.ok(
                ApiResponse.success("Active shifts fetched successfully",
                        shiftService.getActiveShifts())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> getShiftById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Shift fetched successfully",
                        shiftService.getShiftById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> createShift(
            @Valid @RequestBody ShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Shift created successfully",
                        shiftService.createShift(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> updateShift(
            @PathVariable Long id,
            @Valid @RequestBody ShiftUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Shift updated successfully",
                        shiftService.updateShift(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShift(
            @PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok(
                ApiResponse.success("Shift deleted successfully")
        );
    }
}