package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductCategoryResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.ProductCategory;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductCategoryRepository;
import com.ceylonechocolate.chocolate_factory_api.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    @Override
    public List<ProductCategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponse getCategoryById(Long id) {
        return mapToResponse(findCategoryById(id));
    }

    @Override
    @Transactional
    public ProductCategoryResponse createCategory(
            ProductCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + request.getName()
            );
        }

        ProductCategory category = ProductCategory.builder()
                .name(request.getName())
                .codePrefix(request.getCodePrefix())
                .description(request.getDescription())
                .build();

        categoryRepository.save(category);
        log.info("Product category created: {}", category.getName());

        return mapToResponse(category);
    }

    @Override
    @Transactional
    public ProductCategoryResponse updateCategory(
            Long id, ProductCategoryRequest request) {

        ProductCategory category = findCategoryById(id);

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + request.getName()
            );
        }

        category.setName(request.getName());
        category.setCodePrefix(request.getCodePrefix());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);

        log.info("Product category updated: {}", category.getName());
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = findCategoryById(id);
        categoryRepository.delete(category);
        log.info("Product category deleted: {}", category.getName());
    }

    private ProductCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with id: " + id
                        )
                );
    }

    private ProductCategoryResponse mapToResponse(
            ProductCategory category) {
        return ProductCategoryResponse.builder()
                .id(category.getId())
                .codePrefix(category.getCodePrefix())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}