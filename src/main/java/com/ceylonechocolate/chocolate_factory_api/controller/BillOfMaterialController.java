package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BillOfMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BillOfMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.service.BillOfMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bill-of-materials")
@RequiredArgsConstructor
public class BillOfMaterialController {

    private final BillOfMaterialService bomService;

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<BillOfMaterialResponse>>>
    getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                ApiResponse.success("Bill of materials fetched successfully",
                        bomService.getByProduct(productId))
        );
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<BillOfMaterialResponse>>>
    getByRawMaterial(@PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(
                ApiResponse.success("Products using this material fetched",
                        bomService.getByRawMaterial(rawMaterialId))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<BillOfMaterialResponse>> addBomItem(
            @Valid @RequestBody BillOfMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("BOM item added successfully",
                        bomService.addBomItem(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<BillOfMaterialResponse>>
    updateBomItem(@PathVariable Long id,
                  @Valid @RequestBody BillOfMaterialRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("BOM item updated successfully",
                        bomService.updateBomItem(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteBomItem(
            @PathVariable Long id) {
        bomService.deleteBomItem(id);
        return ResponseEntity.ok(
                ApiResponse.success("BOM item deleted successfully")
        );
    }
}