package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderPaymentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PurchaseOrderResponse;
import com.ceylonechocolate.chocolate_factory_api.service.PurchaseOrderService;
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
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','FINANCE_MANAGER'," +
            "'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>>
    getAllPurchaseOrders() {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase orders fetched successfully",
                        purchaseOrderService.getAllPurchaseOrders())
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>>
    getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase orders fetched successfully",
                        purchaseOrderService
                                .getPurchaseOrdersByStatus(status))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER','FINANCE_MANAGER'," +
            "'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order fetched successfully",
                        purchaseOrderService.getPurchaseOrderById(id))
        );
    }

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<String>> getNextPoNumber() {
        return ResponseEntity.ok(
                ApiResponse.success("Next PO number generated",
                        purchaseOrderService.getNextPoNumber())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Purchase order created successfully",
                        purchaseOrderService.createPurchaseOrder(
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    updatePurchaseOrder(@PathVariable Long id,
                        @Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order updated successfully",
                        purchaseOrderService.updatePurchaseOrder(id, request))
        );
    }

    @PutMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    submitForApproval(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order submitted for approval",
                        purchaseOrderService.submitForApproval(id))
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    approvePurchaseOrder(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order approved successfully",
                        purchaseOrderService.approvePurchaseOrder(
                                id, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    rejectPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order rejected",
                        purchaseOrderService.rejectPurchaseOrder(id))
        );
    }

    @PutMapping("/{id}/order")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER'," +
            "'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    markAsOrdered(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order marked as ordered",
                        purchaseOrderService.markAsOrdered(id))
        );
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    cancelPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order cancelled",
                        purchaseOrderService.cancelPurchaseOrder(id))
        );
    }

    @PutMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>>
    updatePayment(@PathVariable Long id,
                  @Valid @RequestBody PurchaseOrderPaymentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Payment updated successfully",
                        purchaseOrderService.updatePayment(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(
            @PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(
                ApiResponse.success("Purchase order deleted successfully")
        );
    }
}