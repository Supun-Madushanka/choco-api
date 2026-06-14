package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.EmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.UpdateEmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.EmployeeResponse;
import com.ceylonechocolate.chocolate_factory_api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> employees =
                employeeService.getAllEmployees();
        return ResponseEntity.ok(
                ApiResponse.success("Employees fetched successfully", employees)
        );
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @PathVariable Long departmentId) {
        List<EmployeeResponse> employees =
                employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(
                ApiResponse.success("Employees fetched successfully", employees)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee fetched successfully", employee)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByUserId(
            @PathVariable Long userId) {
        EmployeeResponse employee =
                employeeService.getEmployeeByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Employee fetched successfully", employee)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee =
                employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Employee created successfully", employee)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse employee =
                employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Employee updated successfully", employee)
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(
            @PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee deactivated successfully")
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> activateEmployee(
            @PathVariable Long id) {
        employeeService.activateEmployee(id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee activated successfully")
        );
    }

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<String>> getNextEmployeeNumber(
            @RequestParam Long departmentId) {

        String nextNumber = employeeService
                .getNextEmployeeNumber(departmentId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Next employee number generated",
                        nextNumber
                )
        );
    }
}