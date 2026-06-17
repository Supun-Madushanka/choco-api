package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.StockMovementRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.StockMovementResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.*;
import com.ceylonechocolate.chocolate_factory_api.repository.*;
import com.ceylonechocolate.chocolate_factory_api.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final WarehouseRepository warehouseRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StockMovementResponse recordMovement(
            StockMovementRequest request, String movedByEmail) {

        // Load entities
        Warehouse warehouse = warehouseRepository
                .findById(request.getWarehouseId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Warehouse not found")
                );

        RawMaterial rawMaterial = rawMaterialRepository
                .findById(request.getRawMaterialId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Raw material not found")
                );

        User movedBy = userRepository
                .findByEmailAndIsDeletedFalse(movedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        StockMovement.MovementType movementType =
                StockMovement.MovementType.valueOf(request.getMovementType());

        // Get or create warehouse stock record
        WarehouseStock stock = warehouseStockRepository
                .findByWarehouseIdAndRawMaterialId(
                        warehouse.getId(), rawMaterial.getId())
                .orElse(WarehouseStock.builder()
                        .warehouse(warehouse)
                        .rawMaterial(rawMaterial)
                        .quantity(BigDecimal.ZERO)
                        .build());

        // Update stock quantity based on movement type
        BigDecimal currentQuantity = stock.getQuantity();
        BigDecimal newQuantity;

        switch (movementType) {
            case IN:
                newQuantity = currentQuantity.add(request.getQuantity());
                break;
            case OUT:
                newQuantity = currentQuantity.subtract(request.getQuantity());
                if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Insufficient stock. Available: " +
                                    currentQuantity + " " +
                                    rawMaterial.getUnit().name()
                    );
                }
                break;
            case ADJUSTMENT:
                // Adjustment sets the quantity directly
                newQuantity = request.getQuantity();
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid movement type: " + movementType
                );
        }

        stock.setQuantity(newQuantity);
        warehouseStockRepository.save(stock);

        // Record movement
        StockMovement movement = StockMovement.builder()
                .warehouse(warehouse)
                .rawMaterial(rawMaterial)
                .movementType(movementType)
                .quantity(request.getQuantity())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .note(request.getNote())
                .movedBy(movedBy)
                .build();

        stockMovementRepository.save(movement);

        log.info("Stock movement recorded: {} {} {} in warehouse {}",
                movementType, request.getQuantity(),
                rawMaterial.getName(), warehouse.getName());

        return mapToResponse(movement, newQuantity);
    }

    @Override
    public List<StockMovementResponse> getAllMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(m -> mapToResponse(m, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementResponse> getMovementsByWarehouse(
            Long warehouseId) {
        return stockMovementRepository
                .findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream()
                .map(m -> mapToResponse(m, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementResponse> getMovementsByRawMaterial(
            Long rawMaterialId) {
        return stockMovementRepository
                .findByRawMaterialIdOrderByCreatedAtDesc(rawMaterialId)
                .stream()
                .map(m -> mapToResponse(m, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementResponse> getMovementsByWarehouseAndMaterial(
            Long warehouseId, Long rawMaterialId) {
        return stockMovementRepository
                .findByWarehouseIdAndRawMaterialIdOrderByCreatedAtDesc(
                        warehouseId, rawMaterialId)
                .stream()
                .map(m -> mapToResponse(m, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementResponse> getMovementsByType(
            String movementType) {
        return stockMovementRepository
                .findByMovementTypeOrderByCreatedAtDesc(
                        StockMovement.MovementType.valueOf(movementType))
                .stream()
                .map(m -> mapToResponse(m, null))
                .collect(Collectors.toList());
    }

    private StockMovementResponse mapToResponse(
            StockMovement movement, BigDecimal stockAfter) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .warehouseId(movement.getWarehouse().getId())
                .warehouseName(movement.getWarehouse().getName())
                .rawMaterialId(movement.getRawMaterial().getId())
                .rawMaterialName(movement.getRawMaterial().getName())
                .unit(movement.getRawMaterial().getUnit().name())
                .movementType(movement.getMovementType().name())
                .quantity(movement.getQuantity())
                .stockAfterMovement(stockAfter)
                .referenceId(movement.getReferenceId())
                .referenceType(movement.getReferenceType())
                .note(movement.getNote())
                .movedByName(movement.getMovedBy().getFullName())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}