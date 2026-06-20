package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BillOfMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BillOfMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.BillOfMaterial;
import com.ceylonechocolate.chocolate_factory_api.entity.Product;
import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterial;
import com.ceylonechocolate.chocolate_factory_api.repository.BillOfMaterialRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.RawMaterialRepository;
import com.ceylonechocolate.chocolate_factory_api.service.BillOfMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillOfMaterialServiceImpl implements BillOfMaterialService {

    private final BillOfMaterialRepository bomRepository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    public List<BillOfMaterialResponse> getByProduct(Long productId) {
        return bomRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillOfMaterialResponse> getByRawMaterial(
            Long rawMaterialId) {
        return bomRepository.findByRawMaterialId(rawMaterialId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillOfMaterialResponse addBomItem(
            BillOfMaterialRequest request) {

        if (bomRepository.existsByProductIdAndRawMaterialId(
                request.getProductId(), request.getRawMaterialId())) {
            throw new IllegalArgumentException(
                    "This raw material is already part of the recipe for this product"
            );
        }

        Product product = productRepository
                .findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found")
                );

        RawMaterial rawMaterial = rawMaterialRepository
                .findById(request.getRawMaterialId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Raw material not found")
                );

        BillOfMaterial bom = BillOfMaterial.builder()
                .product(product)
                .rawMaterial(rawMaterial)
                .quantityRequired(request.getQuantityRequired())
                .unit(BillOfMaterial.Unit.valueOf(request.getUnit()))
                .notes(request.getNotes())
                .build();

        bomRepository.save(bom);
        log.info("BOM item added: {} - {}",
                product.getCode(), rawMaterial.getName());

        return mapToResponse(bom);
    }

    @Override
    @Transactional
    public BillOfMaterialResponse updateBomItem(
            Long id, BillOfMaterialRequest request) {

        BillOfMaterial bom = findById(id);

        bom.setQuantityRequired(request.getQuantityRequired());
        bom.setUnit(BillOfMaterial.Unit.valueOf(request.getUnit()));
        bom.setNotes(request.getNotes());

        bomRepository.save(bom);
        log.info("BOM item updated: id {}", id);

        return mapToResponse(bom);
    }

    @Override
    @Transactional
    public void deleteBomItem(Long id) {
        BillOfMaterial bom = findById(id);
        bomRepository.delete(bom);
        log.info("BOM item deleted: id {}", id);
    }

    private BillOfMaterial findById(Long id) {
        return bomRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "BOM item not found with id: " + id
                        )
                );
    }

    private BillOfMaterialResponse mapToResponse(BillOfMaterial bom) {
        return BillOfMaterialResponse.builder()
                .id(bom.getId())
                .productId(bom.getProduct().getId())
                .productName(bom.getProduct().getName())
                .productCode(bom.getProduct().getCode())
                .rawMaterialId(bom.getRawMaterial().getId())
                .rawMaterialName(bom.getRawMaterial().getName())
                .rawMaterialUnit(bom.getRawMaterial().getUnit().name())
                .quantityRequired(bom.getQuantityRequired())
                .unit(bom.getUnit().name())
                .notes(bom.getNotes())
                .createdAt(bom.getCreatedAt())
                .updatedAt(bom.getUpdatedAt())
                .build();
    }
}