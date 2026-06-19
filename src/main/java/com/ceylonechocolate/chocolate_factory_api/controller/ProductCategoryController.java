package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductCategoryResponse;
import com.ceylonechocolate.chocolate_factory_api.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR','SALES_MANAGER','SALES_OFFICER')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>>
    getAllCategories() {
        return ResponseEntity.ok(
                ApiResponse.success("Categories fetched successfully",
                        categoryService.getAllCategories())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>>
    getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Category fetched successfully",
                        categoryService.getCategoryById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>>
    createCategory(@Valid @RequestBody ProductCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Category created successfully",
                        categoryService.createCategory(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>>
    updateCategory(@PathVariable Long id,
                   @Valid @RequestBody ProductCategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully",
                        categoryService.updateCategory(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ApiResponse.success("Category deleted successfully")
        );
    }
}