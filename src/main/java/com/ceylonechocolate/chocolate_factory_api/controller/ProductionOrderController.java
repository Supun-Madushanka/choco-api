package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductionOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionOrderResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ProductionOrderService;
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
@RequestMapping("/api/v1/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductionOrderResponse>>>
    getAllOrders() {
        return ResponseEntity.ok(
                ApiResponse.success("Production orders fetched successfully",
                        productionOrderService.getAllOrders())
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductionOrderResponse>>>
    getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                ApiResponse.success("Production orders fetched successfully",
                        productionOrderService.getOrdersByStatus(status))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order fetched successfully",
                        productionOrderService.getOrderById(id))
        );
    }

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<String>> getNextOrderNumber() {
        return ResponseEntity.ok(
                ApiResponse.success("Next order number generated",
                        productionOrderService.getNextOrderNumber())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    createOrder(@Valid @RequestBody ProductionOrderRequest request,
                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Production order created successfully",
                        productionOrderService.createOrder(
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    updateOrder(@PathVariable Long id,
                @Valid @RequestBody ProductionOrderRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order updated successfully",
                        productionOrderService.updateOrder(id, request))
        );
    }

    @PutMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    submitForApproval(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order submitted for approval",
                        productionOrderService.submitForApproval(id))
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    approveOrder(@PathVariable Long id,
                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order approved successfully",
                        productionOrderService.approveOrder(
                                id, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    rejectOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order rejected",
                        productionOrderService.rejectOrder(id))
        );
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>>
    cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Production order cancelled",
                        productionOrderService.cancelOrder(id))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long id) {
        productionOrderService.deleteOrder(id);
        return ResponseEntity.ok(
                ApiResponse.success("Production order deleted successfully")
        );
    }
}