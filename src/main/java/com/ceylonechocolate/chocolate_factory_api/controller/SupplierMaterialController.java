package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.service.SupplierMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier-materials")
@RequiredArgsConstructor
public class SupplierMaterialController {

    private final SupplierMaterialService supplierMaterialService;

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierMaterialResponse>>>
    getMaterialsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(
                ApiResponse.success("Materials fetched successfully",
                        supplierMaterialService
                                .getMaterialsBySupplier(supplierId))
        );
    }

    @GetMapping("/material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierMaterialResponse>>>
    getSuppliersByMaterial(@PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Suppliers fetched successfully",
                        supplierMaterialService
                                .getSuppliersByMaterial(rawMaterialId))
        );
    }

    @GetMapping("/material/{rawMaterialId}/preferred")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<SupplierMaterialResponse>>>
    getPreferredSuppliersByMaterial(
            @PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Preferred suppliers fetched successfully",
                        supplierMaterialService
                                .getPreferredSuppliersByMaterial(
                                        rawMaterialId))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierMaterialResponse>>
    addSupplierMaterial(
            @Valid @RequestBody SupplierMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Supplier material added successfully",
                        supplierMaterialService
                                .addSupplierMaterial(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierMaterialResponse>>
    updateSupplierMaterial(@PathVariable Long id,
                           @Valid @RequestBody SupplierMaterialRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier material updated successfully",
                        supplierMaterialService
                                .updateSupplierMaterial(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplierMaterial(
            @PathVariable Long id) {
        supplierMaterialService.deleteSupplierMaterial(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier material deleted successfully")
        );
    }
}