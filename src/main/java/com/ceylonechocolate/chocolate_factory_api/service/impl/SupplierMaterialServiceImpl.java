package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierMaterialResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterial;
import com.ceylonechocolate.chocolate_factory_api.entity.Supplier;
import com.ceylonechocolate.chocolate_factory_api.entity.SupplierMaterial;
import com.ceylonechocolate.chocolate_factory_api.repository.RawMaterialRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.SupplierMaterialRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.SupplierRepository;
import com.ceylonechocolate.chocolate_factory_api.service.SupplierMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierMaterialServiceImpl
        implements SupplierMaterialService {

    private final SupplierMaterialRepository supplierMaterialRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    public List<SupplierMaterialResponse> getMaterialsBySupplier(
            Long supplierId) {
        return supplierMaterialRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierMaterialResponse> getSuppliersByMaterial(
            Long rawMaterialId) {
        return supplierMaterialRepository
                .findByRawMaterialId(rawMaterialId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierMaterialResponse> getPreferredSuppliersByMaterial(
            Long rawMaterialId) {
        return supplierMaterialRepository
                .findByRawMaterialIdAndIsPreferredTrue(rawMaterialId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierMaterialResponse addSupplierMaterial(
            SupplierMaterialRequest request) {

        if (supplierMaterialRepository
                .existsBySupplierIdAndRawMaterialId(
                        request.getSupplierId(),
                        request.getRawMaterialId())) {
            throw new IllegalArgumentException(
                    "This material is already linked to this supplier"
            );
        }

        Supplier supplier = supplierRepository
                .findByIdAndIsDeletedFalse(request.getSupplierId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Supplier not found")
                );

        RawMaterial rawMaterial = rawMaterialRepository
                .findById(request.getRawMaterialId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Raw material not found")
                );

        SupplierMaterial supplierMaterial = SupplierMaterial.builder()
                .supplier(supplier)
                .rawMaterial(rawMaterial)
                .unitPrice(request.getUnitPrice())
                .currency(request.getCurrency() != null
                        ? request.getCurrency() : "LKR")
                .leadTimeDays(request.getLeadTimeDays())
                .isPreferred(request.getIsPreferred() != null
                        ? request.getIsPreferred() : false)
                .build();

        supplierMaterialRepository.save(supplierMaterial);
        log.info("Supplier material added: {} - {}",
                supplier.getCode(), rawMaterial.getName());

        return mapToResponse(supplierMaterial);
    }

    @Override
    @Transactional
    public SupplierMaterialResponse updateSupplierMaterial(
            Long id, SupplierMaterialRequest request) {

        SupplierMaterial supplierMaterial = findById(id);

        supplierMaterial.setUnitPrice(request.getUnitPrice());
        if (request.getCurrency() != null) {
            supplierMaterial.setCurrency(request.getCurrency());
        }
        supplierMaterial.setLeadTimeDays(request.getLeadTimeDays());
        if (request.getIsPreferred() != null) {
            supplierMaterial.setIsPreferred(request.getIsPreferred());
        }

        supplierMaterialRepository.save(supplierMaterial);
        log.info("Supplier material updated: id {}", id);

        return mapToResponse(supplierMaterial);
    }

    @Override
    @Transactional
    public void deleteSupplierMaterial(Long id) {
        SupplierMaterial supplierMaterial = findById(id);
        supplierMaterialRepository.delete(supplierMaterial);
        log.info("Supplier material deleted: id {}", id);
    }

    private SupplierMaterial findById(Long id) {
        return supplierMaterialRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Supplier material not found with id: " + id
                        )
                );
    }

    private SupplierMaterialResponse mapToResponse(
            SupplierMaterial sm) {
        return SupplierMaterialResponse.builder()
                .id(sm.getId())
                .supplierId(sm.getSupplier().getId())
                .supplierCode(sm.getSupplier().getCode())
                .supplierName(sm.getSupplier().getName())
                .rawMaterialId(sm.getRawMaterial().getId())
                .rawMaterialName(sm.getRawMaterial().getName())
                .unit(sm.getRawMaterial().getUnit().name())
                .unitPrice(sm.getUnitPrice())
                .currency(sm.getCurrency())
                .leadTimeDays(sm.getLeadTimeDays())
                .isPreferred(sm.getIsPreferred())
                .createdAt(sm.getCreatedAt())
                .updatedAt(sm.getUpdatedAt())
                .build();
    }
}