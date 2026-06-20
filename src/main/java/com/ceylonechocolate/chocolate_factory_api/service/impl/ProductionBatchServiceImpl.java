package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.*;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionBatchResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.*;
import com.ceylonechocolate.chocolate_factory_api.repository.*;
import com.ceylonechocolate.chocolate_factory_api.service.ProductionBatchService;
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
public class ProductionBatchServiceImpl implements ProductionBatchService {

    private final ProductionBatchRepository productionBatchRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final UserRepository userRepository;
    private final StockMovementService stockMovementService;
    private final FinishedGoodsStockRepository finishedGoodsStockRepository;

    @Override
    public List<ProductionBatchResponse> getAllBatches() {
        return productionBatchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionBatchResponse> getBatchesByOrder(
            Long productionOrderId) {
        return productionBatchRepository
                .findByProductionOrderId(productionOrderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductionBatchResponse getBatchById(Long id) {
        return mapToResponse(findBatchById(id));
    }

    @Override
    @Transactional
    public ProductionBatchResponse createBatch(
            ProductionBatchCreateRequest request, String supervisedByEmail) {

        ProductionOrder order = productionOrderRepository
                .findByIdAndIsDeletedFalse(request.getProductionOrderId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Production order not found")
                );

        if (order.getStatus() != ProductionOrder.POStatus.APPROVED &&
                order.getStatus() != ProductionOrder.POStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Batches can only be created for APPROVED or IN_PROGRESS orders"
            );
        }

        Warehouse warehouse = warehouseRepository
                .findById(request.getWarehouseId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Warehouse not found")
                );

        User supervisedBy = userRepository
                .findByEmailAndIsDeletedFalse(supervisedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String batchNumber = getNextBatchNumber();

        ProductionBatch batch = ProductionBatch.builder()
                .batchNumber(batchNumber)
                .productionOrder(order)
                .warehouse(warehouse)
                .status(ProductionBatch.BatchStatus.IN_PROGRESS)
                .qcStatus(ProductionBatch.QcStatus.PENDING)
                .finalStatus(ProductionBatch.FinalStatus.PENDING)
                .supervisedBy(supervisedBy)
                .notes(request.getNotes())
                .quantityRejected(BigDecimal.ZERO)
                .build();

        productionBatchRepository.save(batch);

        // Move order to IN_PROGRESS if it was APPROVED
        if (order.getStatus() == ProductionOrder.POStatus.APPROVED) {
            order.setStatus(ProductionOrder.POStatus.IN_PROGRESS);
            productionOrderRepository.save(order);
        }

        log.info("Production batch created: {}", batchNumber);
        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public ProductionBatchResponse completeProduction(
            Long id, CompleteProductionRequest request, String movedByEmail) {

        ProductionBatch batch = findBatchById(id);

        if (batch.getStatus() != ProductionBatch.BatchStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Only IN_PROGRESS batches can be completed"
            );
        }

        // Record raw material consumption as stock movements (OUT)
        for (BatchConsumptionItemRequest item : request.getConsumptions()) {
            RawMaterial rawMaterial = rawMaterialRepository
                    .findById(item.getRawMaterialId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Raw material not found: " +
                                            item.getRawMaterialId()
                            )
                    );

            if (item.getQuantityConsumed().compareTo(BigDecimal.ZERO) > 0) {
                StockMovementRequest movementRequest = StockMovementRequest
                        .builder()
                        .warehouseId(batch.getWarehouse().getId())
                        .rawMaterialId(rawMaterial.getId())
                        .movementType("OUT")
                        .quantity(item.getQuantityConsumed())
                        .referenceId(batch.getId())
                        .referenceType("PRODUCTION_BATCH")
                        .note("Consumed in production batch " +
                                batch.getBatchNumber())
                        .build();

                stockMovementService.recordMovement(
                        movementRequest, movedByEmail);
            }
        }

        batch.setQuantityProduced(request.getQuantityProduced());
        batch.setQuantityRejected(request.getQuantityRejected() != null
                ? request.getQuantityRejected() : BigDecimal.ZERO);
        batch.setProductionDate(request.getProductionDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setStatus(ProductionBatch.BatchStatus.QC_PENDING);

        productionBatchRepository.save(batch);

        log.info("Production completed for batch: {}", batch.getBatchNumber());
        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public ProductionBatchResponse markQc(
            Long id, BatchQcRequest request, String qcMarkedByEmail) {

        ProductionBatch batch = findBatchById(id);

        if (batch.getStatus() != ProductionBatch.BatchStatus.QC_PENDING) {
            throw new IllegalArgumentException(
                    "Batch must be in QC_PENDING status to mark QC"
            );
        }

        User qcMarkedBy = userRepository
                .findByEmailAndIsDeletedFalse(qcMarkedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        batch.setQcStatus(ProductionBatch.QcStatus
                .valueOf(request.getQcStatus()));
        batch.setQcMarkedBy(qcMarkedBy);
        batch.setQcMarkedAt(LocalDateTime.now());
        batch.setStatus(ProductionBatch.BatchStatus.QC_DONE);

        productionBatchRepository.save(batch);

        log.info("QC marked for batch {}: {}",
                batch.getBatchNumber(), request.getQcStatus());
        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public ProductionBatchResponse finalApprove(
            Long id, BatchFinalApprovalRequest request,
            String approvedByEmail) {

        ProductionBatch batch = findBatchById(id);

        if (batch.getStatus() != ProductionBatch.BatchStatus.QC_DONE) {
            throw new IllegalArgumentException(
                    "Batch must be in QC_DONE status for final approval"
            );
        }

        User approvedBy = userRepository
                .findByEmailAndIsDeletedFalse(approvedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        ProductionBatch.FinalStatus finalStatus =
                ProductionBatch.FinalStatus.valueOf(request.getFinalStatus());

        batch.setFinalStatus(finalStatus);
        batch.setFinalApprovedBy(approvedBy);
        batch.setFinalApprovedAt(LocalDateTime.now());
        if (request.getNotes() != null) {
            batch.setNotes(request.getNotes());
        }

        switch (finalStatus) {
            case APPROVED -> {
                // Add finished goods stock
                addFinishedGoodsStock(batch);
                batch.setStatus(ProductionBatch.BatchStatus.STOCKED);

                // Update production order actual quantity / status
                updateProductionOrderProgress(
                        batch.getProductionOrder().getId());
            }
            case REJECTED -> batch.setStatus(
                    ProductionBatch.BatchStatus.REJECTED);
            case REPROCESS -> batch.setStatus(
                    ProductionBatch.BatchStatus.REPROCESS);
            default -> throw new IllegalArgumentException(
                    "Invalid final status: " + finalStatus
            );
        }

        productionBatchRepository.save(batch);

        log.info("Final approval for batch {}: {}",
                batch.getBatchNumber(), finalStatus);
        return mapToResponse(batch);
    }

    @Override
    public String getNextBatchNumber() {
        return productionBatchRepository.findLastBatchNumber()
                .map(last -> {
                    int lastNumber = Integer.parseInt(
                            last.replace("BAT-", "")
                    );
                    return String.format("BAT-%03d", lastNumber + 1);
                })
                .orElse("BAT-001");
    }


    // HELPERS
    private void addFinishedGoodsStock(ProductionBatch batch) {
        Product product = batch.getProductionOrder().getProduct();
        Warehouse warehouse = batch.getWarehouse();

        FinishedGoodsStock stock = finishedGoodsStockRepository
                .findByProductIdAndWarehouseId(
                        product.getId(), warehouse.getId())
                .orElse(FinishedGoodsStock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(BigDecimal.ZERO)
                        .build());

        stock.setQuantity(stock.getQuantity()
                .add(batch.getQuantityProduced()));

        finishedGoodsStockRepository.save(stock);
    }

    private void updateProductionOrderProgress(Long orderId) {
        ProductionOrder order = productionOrderRepository
                .findById(orderId)
                .orElseThrow();

        BigDecimal totalStocked = productionBatchRepository
                .getTotalStockedQuantityByOrder(orderId);

        order.setActualQuantity(totalStocked);

        if (totalStocked.compareTo(order.getPlannedQuantity()) >= 0) {
            order.setStatus(ProductionOrder.POStatus.COMPLETED);
            order.setActualDate(java.time.LocalDate.now());
        }

        productionOrderRepository.save(order);
    }

    private ProductionBatch findBatchById(Long id) {
        return productionBatchRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Production batch not found with id: " + id
                        )
                );
    }

    private ProductionBatchResponse mapToResponse(ProductionBatch batch) {
        return ProductionBatchResponse.builder()
                .id(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .productionOrderId(batch.getProductionOrder().getId())
                .orderNumber(batch.getProductionOrder().getOrderNumber())
                .productName(batch.getProductionOrder()
                        .getProduct().getName())
                .warehouseId(batch.getWarehouse().getId())
                .warehouseName(batch.getWarehouse().getName())
                .quantityProduced(batch.getQuantityProduced())
                .quantityRejected(batch.getQuantityRejected())
                .productionDate(batch.getProductionDate())
                .expiryDate(batch.getExpiryDate())
                .qcStatus(batch.getQcStatus().name())
                .qcMarkedByName(batch.getQcMarkedBy() != null
                        ? batch.getQcMarkedBy().getFullName() : null)
                .qcMarkedAt(batch.getQcMarkedAt())
                .finalStatus(batch.getFinalStatus().name())
                .finalApprovedByName(batch.getFinalApprovedBy() != null
                        ? batch.getFinalApprovedBy().getFullName() : null)
                .finalApprovedAt(batch.getFinalApprovedAt())
                .status(batch.getStatus().name())
                .supervisedByName(batch.getSupervisedBy().getFullName())
                .notes(batch.getNotes())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }
}