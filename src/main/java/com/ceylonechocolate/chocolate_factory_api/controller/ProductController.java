package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR','SALES_MANAGER','SALES_OFFICER'," +
            "'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>>
    getAllProducts() {
        return ResponseEntity.ok(
                ApiResponse.success("Products fetched successfully",
                        productService.getAllProducts())
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'SALES_MANAGER','SALES_OFFICER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>>
    getActiveProducts() {
        return ResponseEntity.ok(
                ApiResponse.success("Active products fetched successfully",
                        productService.getActiveProducts())
        );
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>>
    getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success("Products fetched successfully",
                        productService.getProductsByCategory(categoryId))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'SALES_MANAGER','WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully",
                        productService.getProductById(id))
        );
    }

    @GetMapping("/next-code")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<String>> getNextProductCode(
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success("Next product code generated",
                        productService.getNextProductCode(categoryId))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Product created successfully",
                        productService.createProduct(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully",
                        productService.updateProduct(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully")
        );
    }
}