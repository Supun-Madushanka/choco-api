package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PayrollRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PayrollResponse;
import com.ceylonechocolate.chocolate_factory_api.service.PayrollService;
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
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // HR — manage payroll
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<PayrollResponse>> createPayroll(
            @Valid @RequestBody PayrollRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Payroll created successfully",
                        payrollService.createPayroll(
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<PayrollResponse>> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Payroll updated successfully",
                        payrollService.updatePayroll(
                                id, request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<PayrollResponse>> markAsPaid(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Payroll marked as paid",
                        payrollService.markAsPaid(id))
        );
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<PayrollResponse>>> getByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                ApiResponse.success("Payroll fetched successfully",
                        payrollService.getPayrollByEmployee(employeeId))
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<PayrollResponse>>> getByMonthYear(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(
                ApiResponse.success("Payroll fetched successfully",
                        payrollService.getPayrollByMonthAndYear(month, year))
        );
    }

    // every logged in employee
    @GetMapping("/my/latest")
    public ResponseEntity<ApiResponse<PayrollResponse>> getMyLatest(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Latest payroll fetched",
                        payrollService.getMyPayrollLatest(
                                userDetails.getUsername()))
        );
    }

    @GetMapping("/my/history")
    public ResponseEntity<ApiResponse<List<PayrollResponse>>> getMyHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Payroll history fetched",
                        payrollService.getMyPayrollHistory(
                                userDetails.getUsername()))
        );
    }
}