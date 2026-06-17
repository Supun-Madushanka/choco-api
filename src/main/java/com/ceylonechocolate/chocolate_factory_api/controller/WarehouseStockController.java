package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseStockResponse;
import com.ceylonechocolate.chocolate_factory_api.service.WarehouseStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-stock")
@RequiredArgsConstructor
public class WarehouseStockController {

    private final WarehouseStockService warehouseStockService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<WarehouseStockResponse>>>
    getAllStock() {
        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully",
                        warehouseStockService.getAllStock())
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<WarehouseStockResponse>>>
    getStockByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully",
                        warehouseStockService.getStockByWarehouse(warehouseId))
        );
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER'," +
            "'PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<WarehouseStockResponse>>>
    getStockByRawMaterial(@PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully",
                        warehouseStockService
                                .getStockByRawMaterial(rawMaterialId))
        );
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'PROCUREMENT_MANAGER','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<WarehouseStockResponse>>>
    getLowStockItems() {
        return ResponseEntity.ok(
                ApiResponse.success("Low stock items fetched successfully",
                        warehouseStockService.getLowStockItems())
        );
    }

    @GetMapping("/warehouse/{warehouseId}/material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<WarehouseStockResponse>>
    getStockByWarehouseAndMaterial(
            @PathVariable Long warehouseId,
            @PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully",
                        warehouseStockService.getStockByWarehouseAndMaterial(
                                warehouseId, rawMaterialId))
        );
    }
}