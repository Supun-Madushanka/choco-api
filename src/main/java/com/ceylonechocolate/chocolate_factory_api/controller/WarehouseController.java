package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.WarehouseRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseResponse;
import com.ceylonechocolate.chocolate_factory_api.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        return ResponseEntity.ok(
                ApiResponse.success("Warehouses fetched successfully",
                        warehouseService.getAllWarehouses())
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getActiveWarehouses() {
        return ResponseEntity.ok(
                ApiResponse.success("Active warehouses fetched successfully",
                        warehouseService.getActiveWarehouses())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse fetched successfully",
                        warehouseService.getWarehouseById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Warehouse created successfully",
                        warehouseService.createWarehouse(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse updated successfully",
                        warehouseService.updateWarehouse(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(
            @PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse deleted successfully")
        );
    }
}