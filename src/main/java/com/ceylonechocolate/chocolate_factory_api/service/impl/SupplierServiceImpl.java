package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Supplier;
import com.ceylonechocolate.chocolate_factory_api.repository.SupplierRepository;
import com.ceylonechocolate.chocolate_factory_api.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierResponse> getActiveSuppliers() {
        return supplierRepository
                .findByStatusAndIsDeletedFalse(Supplier.SupplierStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        return mapToResponse(findSupplierById(id));
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {

        if (supplierRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Supplier already exists: " + request.getName()
            );
        }

        String code = getNextSupplierCode();

        Supplier supplier = Supplier.builder()
                .code(code)
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .supplierType(Supplier.SupplierType
                        .valueOf(request.getSupplierType()))
                .status(request.getStatus() != null
                        ? Supplier.SupplierStatus.valueOf(request.getStatus())
                        : Supplier.SupplierStatus.ACTIVE)
                .isDeleted(false)
                .build();

        supplierRepository.save(supplier);
        log.info("Supplier created: {}", supplier.getCode());

        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {

        Supplier supplier = findSupplierById(id);

        if (!supplier.getName().equals(request.getName()) &&
                supplierRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Supplier already exists: " + request.getName()
            );
        }

        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setCountry(request.getCountry());
        supplier.setSupplierType(Supplier.SupplierType
                .valueOf(request.getSupplierType()));
        if (request.getStatus() != null) {
            supplier.setStatus(Supplier.SupplierStatus
                    .valueOf(request.getStatus()));
        }

        supplierRepository.save(supplier);
        log.info("Supplier updated: {}", supplier.getCode());

        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = findSupplierById(id);
        supplier.setIsDeleted(true);
        supplier.setDeletedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
        log.info("Supplier deleted: {}", supplier.getCode());
    }

    @Override
    public String getNextSupplierCode() {
        return supplierRepository.findLastCode()
                .map(lastCode -> {
                    // Extract number from "SUP-009" → 9
                    int lastNumber = Integer.parseInt(
                            lastCode.replace("SUP-", "")
                    );
                    return String.format("SUP-%03d", lastNumber + 1);
                })
                .orElse("SUP-001");
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Supplier not found with id: " + id
                        )
                );
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .country(supplier.getCountry())
                .supplierType(supplier.getSupplierType().name())
                .status(supplier.getStatus().name())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}