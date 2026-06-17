package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialCategoryResponse;

import java.util.List;

public interface RawMaterialCategoryService {

    List<RawMaterialCategoryResponse> getAllCategories();

    RawMaterialCategoryResponse getCategoryById(Long id);

    RawMaterialCategoryResponse createCategory(
            RawMaterialCategoryRequest request);

    RawMaterialCategoryResponse updateCategory(
            Long id, RawMaterialCategoryRequest request);

    void deleteCategory(Long id);
}