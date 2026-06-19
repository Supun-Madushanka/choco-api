package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {

    List<ProductCategoryResponse> getAllCategories();

    ProductCategoryResponse getCategoryById(Long id);

    ProductCategoryResponse createCategory(ProductCategoryRequest request);

    ProductCategoryResponse updateCategory(Long id, ProductCategoryRequest request);

    void deleteCategory(Long id);
}