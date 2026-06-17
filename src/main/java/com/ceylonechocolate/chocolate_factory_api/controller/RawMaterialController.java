package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER','PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<RawMaterialResponse>>>
    getAllRawMaterials() {
        return ResponseEntity.ok(
                ApiResponse.success("Raw materials fetched successfully",
                        rawMaterialService.getAllRawMaterials())
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER','PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<RawMaterialResponse>>>
    getActiveRawMaterials() {
        return ResponseEntity.ok(
                ApiResponse.success("Active raw materials fetched successfully",
                        rawMaterialService.getActiveRawMaterials())
        );
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'PROCUREMENT_MANAGER','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<RawMaterialResponse>>>
    getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success("Raw materials fetched successfully",
                        rawMaterialService.getRawMaterialsByCategory(categoryId))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<RawMaterialResponse>>
    getRawMaterialById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Raw material fetched successfully",
                        rawMaterialService.getRawMaterialById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RawMaterialResponse>>
    createRawMaterial(
            @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Raw material created successfully",
                        rawMaterialService.createRawMaterial(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RawMaterialResponse>>
    updateRawMaterial(@PathVariable Long id,
                      @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Raw material updated successfully",
                        rawMaterialService.updateRawMaterial(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRawMaterial(
            @PathVariable Long id) {
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.ok(
                ApiResponse.success("Raw material deleted successfully")
        );
    }
}