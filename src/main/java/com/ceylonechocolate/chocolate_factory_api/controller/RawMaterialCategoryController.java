package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialCategoryResponse;
import com.ceylonechocolate.chocolate_factory_api.service.RawMaterialCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/raw-material-categories")
@RequiredArgsConstructor
public class RawMaterialCategoryController {

    private final RawMaterialCategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','WAREHOUSE_STAFF'," +
            "'PROCUREMENT_MANAGER','PROCUREMENT_OFFICER'," +
            "'PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<RawMaterialCategoryResponse>>>
    getAllCategories() {
        return ResponseEntity.ok(
                ApiResponse.success("Categories fetched successfully",
                        categoryService.getAllCategories())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','WAREHOUSE_MANAGER'," +
            "'WAREHOUSE_SUPERVISOR','PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<RawMaterialCategoryResponse>>
    getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Category fetched successfully",
                        categoryService.getCategoryById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RawMaterialCategoryResponse>>
    createCategory(@Valid @RequestBody RawMaterialCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Category created successfully",
                        categoryService.createCategory(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RawMaterialCategoryResponse>>
    updateCategory(@PathVariable Long id,
                   @Valid @RequestBody RawMaterialCategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully",
                        categoryService.updateCategory(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ApiResponse.success("Category deleted successfully")
        );
    }
}