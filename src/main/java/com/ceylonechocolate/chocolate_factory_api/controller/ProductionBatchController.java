package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.*;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionBatchResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ProductionBatchService;
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
@RequestMapping("/api/v1/production-batches")
@RequiredArgsConstructor
public class ProductionBatchController {

    private final ProductionBatchService productionBatchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR','QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<List<ProductionBatchResponse>>>
    getAllBatches() {
        return ResponseEntity.ok(
                ApiResponse.success("Production batches fetched successfully",
                        productionBatchService.getAllBatches())
        );
    }

    @GetMapping("/order/{productionOrderId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductionBatchResponse>>>
    getByOrder(@PathVariable Long productionOrderId) {
        return ResponseEntity.ok(
                ApiResponse.success("Production batches fetched successfully",
                        productionBatchService.getBatchesByOrder(
                                productionOrderId))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR','QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>>
    getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Production batch fetched successfully",
                        productionBatchService.getBatchById(id))
        );
    }

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<String>> getNextBatchNumber() {
        return ResponseEntity.ok(
                ApiResponse.success("Next batch number generated",
                        productionBatchService.getNextBatchNumber())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>>
    createBatch(@Valid @RequestBody ProductionBatchCreateRequest request,
                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Production batch created successfully",
                        productionBatchService.createBatch(
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/complete-production")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>>
    completeProduction(@PathVariable Long id,
                       @Valid @RequestBody CompleteProductionRequest request,
                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Production completed successfully",
                        productionBatchService.completeProduction(
                                id, request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/qc")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>>
    markQc(@PathVariable Long id,
           @Valid @RequestBody BatchQcRequest request,
           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("QC marked successfully",
                        productionBatchService.markQc(
                                id, request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/final-approval")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>>
    finalApprove(@PathVariable Long id,
                 @Valid @RequestBody BatchFinalApprovalRequest request,
                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Final approval recorded successfully",
                        productionBatchService.finalApprove(
                                id, request, userDetails.getUsername()))
        );
    }
}