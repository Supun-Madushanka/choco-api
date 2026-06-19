package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.GoodsReceivedNoteRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.GrnItemInspectRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.GoodsReceivedNoteResponse;
import com.ceylonechocolate.chocolate_factory_api.service.GoodsReceivedNoteService;
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
@RequestMapping("/api/v1/grn")
@RequiredArgsConstructor
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService grnService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'QC_MANAGER','QC_CONTROLLER','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<GoodsReceivedNoteResponse>>>
    getAllGrns() {
        return ResponseEntity.ok(
                ApiResponse.success("GRNs fetched successfully",
                        grnService.getAllGrns())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'QC_MANAGER','QC_CONTROLLER','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<GoodsReceivedNoteResponse>>
    getGrnById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("GRN fetched successfully",
                        grnService.getGrnById(id))
        );
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<List<GoodsReceivedNoteResponse>>>
    getByPurchaseOrder(@PathVariable Long purchaseOrderId) {
        return ResponseEntity.ok(
                ApiResponse.success("GRNs fetched successfully",
                        grnService.getGrnsByPurchaseOrder(purchaseOrderId))
        );
    }

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<String>> getNextGrnNumber() {
        return ResponseEntity.ok(
                ApiResponse.success("Next GRN number generated",
                        grnService.getNextGrnNumber())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<GoodsReceivedNoteResponse>>
    createGrn(@Valid @RequestBody GoodsReceivedNoteRequest request,
              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("GRN created successfully",
                        grnService.createGrn(
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/submit-qc")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<GoodsReceivedNoteResponse>>
    submitForQc(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("GRN submitted for QC",
                        grnService.submitForQc(id))
        );
    }

    @PutMapping("/{grnId}/items/{itemId}/inspect")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<GoodsReceivedNoteResponse>>
    inspectItem(@PathVariable Long grnId,
                @PathVariable Long itemId,
                @Valid @RequestBody GrnItemInspectRequest request,
                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("GRN item inspected successfully",
                        grnService.inspectItem(grnId, itemId,
                                request, userDetails.getUsername()))
        );
    }

    @PutMapping("/{id}/complete-qc")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','QC_MANAGER')")
    public ResponseEntity<ApiResponse<GoodsReceivedNoteResponse>>
    completeQc(@PathVariable Long id,
               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("QC completed successfully",
                        grnService.completeQc(id, userDetails.getUsername()))
        );
    }
}