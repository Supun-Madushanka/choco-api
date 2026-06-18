package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierResponse;
import com.ceylonechocolate.chocolate_factory_api.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER'," +
            "'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>>
    getAllSuppliers() {
        return ResponseEntity.ok(
                ApiResponse.success("Suppliers fetched successfully",
                        supplierService.getAllSuppliers())
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>>
    getActiveSuppliers() {
        return ResponseEntity.ok(
                ApiResponse.success("Active suppliers fetched successfully",
                        supplierService.getActiveSuppliers())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<SupplierResponse>>
    getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Supplier fetched successfully",
                        supplierService.getSupplierById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Supplier created successfully",
                        supplierService.createSupplier(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Supplier updated successfully",
                        supplierService.updateSupplier(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(
                ApiResponse.success("Supplier deleted successfully")
        );
    }

    @GetMapping("/next-code")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> getNextSupplierCode() {
        return ResponseEntity.ok(
                ApiResponse.success("Next supplier code generated",
                        supplierService.getNextSupplierCode())
        );
    }
}