package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.FinishedGoodsStockResponse;
import com.ceylonechocolate.chocolate_factory_api.service.FinishedGoodsStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finished-goods-stock")
@RequiredArgsConstructor
public class FinishedGoodsStockController {

    private final FinishedGoodsStockService finishedGoodsStockService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'WAREHOUSE_MANAGER','SALES_MANAGER','SALES_OFFICER')")
    public ResponseEntity<ApiResponse<List<FinishedGoodsStockResponse>>>
    getAllStock() {
        return ResponseEntity.ok(
                ApiResponse.success("Finished goods stock fetched successfully",
                        finishedGoodsStockService.getAllStock())
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'WAREHOUSE_MANAGER','SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<FinishedGoodsStockResponse>>>
    getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(
                ApiResponse.success("Finished goods stock fetched successfully",
                        finishedGoodsStockService
                                .getStockByWarehouse(warehouseId))
        );
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<FinishedGoodsStockResponse>>>
    getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                ApiResponse.success("Finished goods stock fetched successfully",
                        finishedGoodsStockService.getStockByProduct(productId))
        );
    }
}