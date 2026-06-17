package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterial;
import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterialCategory;
import com.ceylonechocolate.chocolate_factory_api.repository.RawMaterialCategoryRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.RawMaterialRepository;
import com.ceylonechocolate.chocolate_factory_api.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialCategoryRepository categoryRepository;

    @Override
    public List<RawMaterialResponse> getAllRawMaterials() {
        return rawMaterialRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RawMaterialResponse> getActiveRawMaterials() {
        return rawMaterialRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RawMaterialResponse> getRawMaterialsByCategory(
            Long categoryId) {
        return rawMaterialRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RawMaterialResponse getRawMaterialById(Long id) {
        return mapToResponse(findRawMaterialById(id));
    }

    @Override
    @Transactional
    public RawMaterialResponse createRawMaterial(RawMaterialRequest request) {

        if (rawMaterialRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Raw material already exists: " + request.getName()
            );
        }

        RawMaterialCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        RawMaterial rawMaterial = RawMaterial.builder()
                .category(category)
                .name(request.getName())
                .unit(RawMaterial.Unit.valueOf(request.getUnit()))
                .minStockLevel(request.getMinStockLevel() != null
                        ? request.getMinStockLevel()
                        : java.math.BigDecimal.ZERO)
                .description(request.getDescription())
                .isActive(request.getIsActive() != null
                        ? request.getIsActive() : true)
                .build();

        rawMaterialRepository.save(rawMaterial);
        log.info("Raw material created: {}", rawMaterial.getName());

        return mapToResponse(rawMaterial);
    }

    @Override
    @Transactional
    public RawMaterialResponse updateRawMaterial(
            Long id, RawMaterialRequest request) {

        RawMaterial rawMaterial = findRawMaterialById(id);

        if (!rawMaterial.getName().equals(request.getName()) &&
                rawMaterialRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Raw material already exists: " + request.getName()
            );
        }

        RawMaterialCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        rawMaterial.setCategory(category);
        rawMaterial.setName(request.getName());
        rawMaterial.setUnit(RawMaterial.Unit.valueOf(request.getUnit()));
        if (request.getMinStockLevel() != null) {
            rawMaterial.setMinStockLevel(request.getMinStockLevel());
        }
        rawMaterial.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            rawMaterial.setIsActive(request.getIsActive());
        }

        rawMaterialRepository.save(rawMaterial);
        log.info("Raw material updated: {}", rawMaterial.getName());

        return mapToResponse(rawMaterial);
    }

    @Override
    @Transactional
    public void deleteRawMaterial(Long id) {
        RawMaterial rawMaterial = findRawMaterialById(id);
        rawMaterialRepository.delete(rawMaterial);
        log.info("Raw material deleted: {}", rawMaterial.getName());
    }

    private RawMaterial findRawMaterialById(Long id) {
        return rawMaterialRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Raw material not found with id: " + id
                        )
                );
    }

    private RawMaterialResponse mapToResponse(RawMaterial rm) {
        return RawMaterialResponse.builder()
                .id(rm.getId())
                .categoryId(rm.getCategory().getId())
                .categoryName(rm.getCategory().getName())
                .name(rm.getName())
                .unit(rm.getUnit().name())
                .minStockLevel(rm.getMinStockLevel())
                .description(rm.getDescription())
                .isActive(rm.getIsActive())
                .createdAt(rm.getCreatedAt())
                .updatedAt(rm.getUpdatedAt())
                .build();
    }
}