package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftAssignmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftAssignmentResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ShiftAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shift-assignments")
@RequiredArgsConstructor
public class ShiftAssignmentController {

    private final ShiftAssignmentService shiftAssignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER'," +
            "'PRODUCTION_MANAGER','PRODUCTION_SUPERVISOR'," +
            "'WAREHOUSE_MANAGER','WAREHOUSE_SUPERVISOR'," +
            "'FINANCE_MANAGER','SALES_MANAGER'," +
            "'PROCUREMENT_MANAGER','QC_MANAGER')")
    public ResponseEntity<ApiResponse<ShiftAssignmentResponse>> assignShift(
            @Valid @RequestBody ShiftAssignmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ShiftAssignmentResponse response =
                shiftAssignmentService.assignShift(
                        request,
                        userDetails.getUsername()
                );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Shift assigned successfully", response)
        );
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<ShiftAssignmentResponse>>>
    getAssignmentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignments fetched successfully",
                        shiftAssignmentService
                                .getAssignmentsByEmployee(employeeId))
        );
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<ShiftAssignmentResponse>>>
    getAssignmentsByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignments fetched successfully",
                        shiftAssignmentService.getAssignmentsByDate(date))
        );
    }

    @GetMapping("/employee/{employeeId}/range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<ShiftAssignmentResponse>>>
    getAssignmentsByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignments fetched successfully",
                        shiftAssignmentService
                                .getAssignmentsByEmployeeAndDateRange(
                                        employeeId, startDate, endDate))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @PathVariable Long id) {
        shiftAssignmentService.deleteAssignment(id);
        return ResponseEntity.ok(
                ApiResponse.success("Shift assignment deleted successfully")
        );
    }
}