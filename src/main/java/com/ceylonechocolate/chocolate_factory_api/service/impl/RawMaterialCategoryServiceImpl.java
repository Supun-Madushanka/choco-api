package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialCategoryRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialCategoryResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterialCategory;
import com.ceylonechocolate.chocolate_factory_api.repository.RawMaterialCategoryRepository;
import com.ceylonechocolate.chocolate_factory_api.service.RawMaterialCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawMaterialCategoryServiceImpl
        implements RawMaterialCategoryService {

    private final RawMaterialCategoryRepository categoryRepository;

    @Override
    public List<RawMaterialCategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RawMaterialCategoryResponse getCategoryById(Long id) {
        return mapToResponse(findCategoryById(id));
    }

    @Override
    @Transactional
    public RawMaterialCategoryResponse createCategory(
            RawMaterialCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + request.getName()
            );
        }

        RawMaterialCategory category = RawMaterialCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        categoryRepository.save(category);
        log.info("Raw material category created: {}", category.getName());

        return mapToResponse(category);
    }

    @Override
    @Transactional
    public RawMaterialCategoryResponse updateCategory(
            Long id, RawMaterialCategoryRequest request) {

        RawMaterialCategory category = findCategoryById(id);

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + request.getName()
            );
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);

        log.info("Raw material category updated: {}", category.getName());
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        RawMaterialCategory category = findCategoryById(id);
        categoryRepository.delete(category);
        log.info("Raw material category deleted: {}", category.getName());
    }

    private RawMaterialCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with id: " + id
                        )
                );
    }

    private RawMaterialCategoryResponse mapToResponse(
            RawMaterialCategory category) {
        return RawMaterialCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}