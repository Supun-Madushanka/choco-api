package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.DepartmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.DepartmentResponse;
import com.ceylonechocolate.chocolate_factory_api.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> departments =
                departmentService.getAllDepartments();
        return ResponseEntity.ok(
                ApiResponse.success("Departments fetched successfully", departments)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable Long id) {
        DepartmentResponse department =
                departmentService.getDepartmentById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Department fetched successfully", department)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department =
                departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Department created successfully", department)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department =
                departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Department updated successfully", department)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.success("Department deleted successfully")
        );
    }
}