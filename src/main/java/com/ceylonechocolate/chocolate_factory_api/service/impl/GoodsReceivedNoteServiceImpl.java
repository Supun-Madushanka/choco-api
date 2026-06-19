package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.GoodsReceivedNoteRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.GrnItemCreateRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.GrnItemInspectRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.StockMovementRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.GoodsReceivedNoteResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.GrnItemResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.*;
import com.ceylonechocolate.chocolate_factory_api.repository.*;
import com.ceylonechocolate.chocolate_factory_api.service.GoodsReceivedNoteService;
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
public class GoodsReceivedNoteServiceImpl
        implements GoodsReceivedNoteService {

    private final GoodsReceivedNoteRepository grnRepository;
    private final GrnItemRepository grnItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final UserRepository userRepository;
    private final StockMovementService stockMovementService;

    @Override
    public List<GoodsReceivedNoteResponse> getAllGrns() {
        return grnRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GoodsReceivedNoteResponse getGrnById(Long id) {
        return mapToResponse(findGrnById(id));
    }

    @Override
    public List<GoodsReceivedNoteResponse> getGrnsByPurchaseOrder(
            Long purchaseOrderId) {
        return grnRepository.findByPurchaseOrderId(purchaseOrderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GoodsReceivedNoteResponse createGrn(
            GoodsReceivedNoteRequest request, String receivedByEmail) {

        PurchaseOrder po = purchaseOrderRepository
                .findByIdAndIsDeletedFalse(request.getPurchaseOrderId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Purchase order not found")
                );

        if (po.getStatus() != PurchaseOrder.POStatus.ORDERED) {
            throw new IllegalArgumentException(
                    "GRN can only be created for ORDERED purchase orders"
            );
        }

        Warehouse warehouse = warehouseRepository
                .findById(request.getWarehouseId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Warehouse not found")
                );

        User receivedBy = userRepository
                .findByEmailAndIsDeletedFalse(receivedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String grnNumber = getNextGrnNumber();

        GoodsReceivedNote grn = GoodsReceivedNote.builder()
                .grnNumber(grnNumber)
                .purchaseOrder(po)
                .warehouse(warehouse)
                .receivedBy(receivedBy)
                .receivedDate(request.getReceivedDate())
                .status(GoodsReceivedNote.GrnStatus.DRAFT)
                .notes(request.getNotes())
                .build();

        grnRepository.save(grn);

        // Create GRN items
        for (GrnItemCreateRequest itemReq : request.getItems()) {
            RawMaterial rawMaterial = rawMaterialRepository
                    .findById(itemReq.getRawMaterialId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Raw material not found: " +
                                            itemReq.getRawMaterialId()
                            )
                    );

            GrnItem grnItem = GrnItem.builder()
                    .grn(grn)
                    .rawMaterial(rawMaterial)
                    .orderedQuantity(itemReq.getOrderedQuantity())
                    .receivedQuantity(itemReq.getReceivedQuantity())
                    .acceptedQuantity(BigDecimal.ZERO)
                    .rejectedQuantity(BigDecimal.ZERO)
                    .qualityStatus(GrnItem.QualityStatus.PENDING)
                    .build();

            grnItemRepository.save(grnItem);
        }

        log.info("GRN created: {}", grnNumber);
        return mapToResponse(grn);
    }

    @Override
    @Transactional
    public GoodsReceivedNoteResponse submitForQc(Long id) {
        GoodsReceivedNote grn = findGrnById(id);

        if (grn.getStatus() != GoodsReceivedNote.GrnStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT GRNs can be submitted for QC"
            );
        }

        grn.setStatus(GoodsReceivedNote.GrnStatus.QC_PENDING);
        grnRepository.save(grn);

        log.info("GRN submitted for QC: {}", grn.getGrnNumber());
        return mapToResponse(grn);
    }

    @Override
    @Transactional
    public GoodsReceivedNoteResponse inspectItem(
            Long grnId, Long itemId,
            GrnItemInspectRequest request, String inspectedByEmail) {

        GoodsReceivedNote grn = findGrnById(grnId);

        if (grn.getStatus() != GoodsReceivedNote.GrnStatus.QC_PENDING) {
            throw new IllegalArgumentException(
                    "GRN must be in QC_PENDING status to inspect items"
            );
        }

        GrnItem item = grnItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("GRN item not found")
                );

        if (!item.getGrn().getId().equals(grnId)) {
            throw new IllegalArgumentException(
                    "This item does not belong to the specified GRN"
            );
        }

        if (request.getAcceptedQuantity()
                .compareTo(item.getReceivedQuantity()) > 0) {
            throw new IllegalArgumentException(
                    "Accepted quantity cannot exceed received quantity of " +
                            item.getReceivedQuantity()
            );
        }

        User inspectedBy = userRepository
                .findByEmailAndIsDeletedFalse(inspectedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        BigDecimal rejectedQuantity = item.getReceivedQuantity()
                .subtract(request.getAcceptedQuantity());

        item.setAcceptedQuantity(request.getAcceptedQuantity());
        item.setRejectedQuantity(rejectedQuantity);
        item.setQualityStatus(GrnItem.QualityStatus
                .valueOf(request.getQualityStatus()));
        item.setQualityNotes(request.getQualityNotes());
        item.setInspectedBy(inspectedBy);
        item.setInspectedAt(LocalDateTime.now());

        grnItemRepository.save(item);

        log.info("GRN item inspected: {} - material {}",
                grn.getGrnNumber(), item.getRawMaterial().getName());

        return mapToResponse(grn);
    }

    @Override
    @Transactional
    public GoodsReceivedNoteResponse completeQc(
            Long id, String movedByEmail) {

        GoodsReceivedNote grn = findGrnById(id);

        if (grn.getStatus() != GoodsReceivedNote.GrnStatus.QC_PENDING) {
            throw new IllegalArgumentException(
                    "GRN must be in QC_PENDING status to complete QC"
            );
        }

        List<GrnItem> items = grnItemRepository.findByGrnId(id);

        // Validate all items inspected
        boolean allInspected = items.stream()
                .allMatch(item ->
                        item.getQualityStatus() != GrnItem.QualityStatus.PENDING
                );

        if (!allInspected) {
            throw new IllegalArgumentException(
                    "All items must be inspected before completing QC"
            );
        }

        // Determine overall status — if ANY item failed, mark whole GRN failed
        boolean anyFailed = items.stream()
                .anyMatch(item ->
                        item.getQualityStatus() == GrnItem.QualityStatus.FAILED
                );

        if (anyFailed) {
            grn.setStatus(GoodsReceivedNote.GrnStatus.QC_FAILED);
            grnRepository.save(grn);
            log.info("GRN QC failed: {}", grn.getGrnNumber());
            return mapToResponse(grn);
        }

        // All passed → record stock movements for accepted quantities
        grn.setStatus(GoodsReceivedNote.GrnStatus.QC_PASSED);
        grnRepository.save(grn);

        for (GrnItem item : items) {
            if (item.getAcceptedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                StockMovementRequest movementRequest = StockMovementRequest
                        .builder()
                        .warehouseId(grn.getWarehouse().getId())
                        .rawMaterialId(item.getRawMaterial().getId())
                        .movementType("IN")
                        .quantity(item.getAcceptedQuantity())
                        .referenceId(grn.getId())
                        .referenceType("GRN")
                        .note("Stock received via " + grn.getGrnNumber())
                        .build();

                stockMovementService.recordMovement(
                        movementRequest, movedByEmail);
            }

            // Update PO item received quantity
            updatePoItemReceivedQuantity(
                    grn.getPurchaseOrder().getId(),
                    item.getRawMaterial().getId(),
                    item.getAcceptedQuantity());
        }

        // Mark GRN as stocked
        grn.setStatus(GoodsReceivedNote.GrnStatus.STOCKED);
        grnRepository.save(grn);

        // Update PO status based on full/partial receipt
        updatePurchaseOrderStatus(grn.getPurchaseOrder().getId());

        log.info("GRN stocked successfully: {}", grn.getGrnNumber());
        return mapToResponse(grn);
    }

    @Override
    public String getNextGrnNumber() {
        return grnRepository.findLastGrnNumber()
                .map(last -> {
                    int lastNumber = Integer.parseInt(
                            last.replace("GRN-", "")
                    );
                    return String.format("GRN-%03d", lastNumber + 1);
                })
                .orElse("GRN-001");
    }

    // HELPERS
    private void updatePoItemReceivedQuantity(
            Long purchaseOrderId, Long rawMaterialId,
            BigDecimal acceptedQuantity) {

        List<PurchaseOrderItem> poItems = purchaseOrderItemRepository
                .findByPurchaseOrderId(purchaseOrderId);

        poItems.stream()
                .filter(item ->
                        item.getRawMaterial().getId().equals(rawMaterialId))
                .findFirst()
                .ifPresent(item -> {
                    BigDecimal newReceived = item.getReceivedQuantity()
                            .add(acceptedQuantity);
                    item.setReceivedQuantity(newReceived);
                    purchaseOrderItemRepository.save(item);
                });
    }

    private void updatePurchaseOrderStatus(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository
                .findById(purchaseOrderId)
                .orElseThrow();

        List<PurchaseOrderItem> items = purchaseOrderItemRepository
                .findByPurchaseOrderId(purchaseOrderId);

        boolean fullyReceived = items.stream()
                .allMatch(item ->
                        item.getReceivedQuantity()
                                .compareTo(item.getQuantity()) >= 0
                );

        if (fullyReceived) {
            po.setStatus(PurchaseOrder.POStatus.RECEIVED);
        } else {
            po.setStatus(PurchaseOrder.POStatus.PARTIALLY_RECEIVED);
        }

        purchaseOrderRepository.save(po);
    }

    private GoodsReceivedNote findGrnById(Long id) {
        return grnRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "GRN not found with id: " + id
                        )
                );
    }

    private GoodsReceivedNoteResponse mapToResponse(
            GoodsReceivedNote grn) {

        List<GrnItemResponse> items = grnItemRepository
                .findByGrnId(grn.getId())
                .stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return GoodsReceivedNoteResponse.builder()
                .id(grn.getId())
                .grnNumber(grn.getGrnNumber())
                .purchaseOrderId(grn.getPurchaseOrder().getId())
                .poNumber(grn.getPurchaseOrder().getPoNumber())
                .warehouseId(grn.getWarehouse().getId())
                .warehouseName(grn.getWarehouse().getName())
                .receivedByName(grn.getReceivedBy().getFullName())
                .receivedDate(grn.getReceivedDate())
                .status(grn.getStatus().name())
                .notes(grn.getNotes())
                .items(items)
                .createdAt(grn.getCreatedAt())
                .updatedAt(grn.getUpdatedAt())
                .build();
    }

    private GrnItemResponse mapItemToResponse(GrnItem item) {
        return GrnItemResponse.builder()
                .id(item.getId())
                .rawMaterialId(item.getRawMaterial().getId())
                .rawMaterialName(item.getRawMaterial().getName())
                .unit(item.getRawMaterial().getUnit().name())
                .orderedQuantity(item.getOrderedQuantity())
                .receivedQuantity(item.getReceivedQuantity())
                .acceptedQuantity(item.getAcceptedQuantity())
                .rejectedQuantity(item.getRejectedQuantity())
                .qualityStatus(item.getQualityStatus().name())
                .qualityNotes(item.getQualityNotes())
                .inspectedByName(item.getInspectedBy() != null
                        ? item.getInspectedBy().getFullName() : null)
                .inspectedAt(item.getInspectedAt())
                .build();
    }
}