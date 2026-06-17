package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.StockMovementRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.StockMovementResponse;
import com.ceylonechocolate.chocolate_factory_api.service.StockMovementService;
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
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<StockMovementResponse>> recordMovement(
            @Valid @RequestBody StockMovementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Stock movement recorded successfully",
                        stockMovementService.recordMovement(
                                request, userDetails.getUsername()))
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>>
    getAllMovements() {
        return ResponseEntity.ok(
                ApiResponse.success("Movements fetched successfully",
                        stockMovementService.getAllMovements())
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>>
    getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(
                ApiResponse.success("Movements fetched successfully",
                        stockMovementService
                                .getMovementsByWarehouse(warehouseId))
        );
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'PROCUREMENT_MANAGER','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>>
    getByRawMaterial(@PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Movements fetched successfully",
                        stockMovementService
                                .getMovementsByRawMaterial(rawMaterialId))
        );
    }

    @GetMapping("/warehouse/{warehouseId}/material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>>
    getByWarehouseAndMaterial(
            @PathVariable Long warehouseId,
            @PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Movements fetched successfully",
                        stockMovementService.getMovementsByWarehouseAndMaterial(
                                warehouseId, rawMaterialId))
        );
    }

    @GetMapping("/type/{movementType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>>
    getByType(@PathVariable String movementType) {
        return ResponseEntity.ok(
                ApiResponse.success("Movements fetched successfully",
                        stockMovementService.getMovementsByType(movementType))
        );
    }
}