package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderItemRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderPaymentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PurchaseOrderItemResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PurchaseOrderResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.*;
import com.ceylonechocolate.chocolate_factory_api.repository.*;
import com.ceylonechocolate.chocolate_factory_api.service.PurchaseOrderService;
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
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final UserRepository userRepository;

    @Override
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository
                .findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderResponse> getPurchaseOrdersByStatus(
            String status) {
        return purchaseOrderRepository
                .findByStatusAndIsDeletedFalse(
                        PurchaseOrder.POStatus.valueOf(status))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        return mapToResponse(findPurchaseOrderById(id));
    }

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(
            PurchaseOrderRequest request, String createdByEmail) {

        Supplier supplier = supplierRepository
                .findByIdAndIsDeletedFalse(request.getSupplierId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Supplier not found")
                );

        User createdBy = userRepository
                .findByEmailAndIsDeletedFalse(createdByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        // Generate PO number
        String poNumber = getNextPoNumber();

        // Build PO
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .poNumber(poNumber)
                .supplier(supplier)
                .createdBy(createdBy)
                .currency(request.getCurrency() != null
                        ? request.getCurrency() : "LKR")
                .expectedDate(request.getExpectedDate())
                .notes(request.getNotes())
                .status(PurchaseOrder.POStatus.DRAFT)
                .paymentStatus(PurchaseOrder.PaymentStatus.UNPAID)
                .paidAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .isDeleted(false)
                .build();

        purchaseOrderRepository.save(purchaseOrder);

        // Build items and calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            RawMaterial rawMaterial = rawMaterialRepository
                    .findById(itemReq.getRawMaterialId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Raw material not found: " +
                                            itemReq.getRawMaterialId()
                            )
                    );

            BigDecimal totalPrice = itemReq.getUnitPrice()
                    .multiply(itemReq.getQuantity());

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .rawMaterial(rawMaterial)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(totalPrice)
                    .receivedQuantity(BigDecimal.ZERO)
                    .build();

            purchaseOrderItemRepository.save(item);
            total = total.add(totalPrice);
        }

        // Update total amount
        purchaseOrder.setTotalAmount(total);
        purchaseOrderRepository.save(purchaseOrder);

        log.info("Purchase order created: {}", poNumber);
        return mapToResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(
            Long id, PurchaseOrderRequest request) {

        PurchaseOrder po = findPurchaseOrderById(id);

        if (po.getStatus() != PurchaseOrder.POStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT purchase orders can be updated"
            );
        }

        Supplier supplier = supplierRepository
                .findByIdAndIsDeletedFalse(request.getSupplierId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Supplier not found")
                );

        po.setSupplier(supplier);
        po.setCurrency(request.getCurrency() != null
                ? request.getCurrency() : "LKR");
        po.setExpectedDate(request.getExpectedDate());
        po.setNotes(request.getNotes());

        // Delete existing items and recreate
        purchaseOrderItemRepository
                .deleteAll(purchaseOrderItemRepository
                        .findByPurchaseOrderId(id));

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            RawMaterial rawMaterial = rawMaterialRepository
                    .findById(itemReq.getRawMaterialId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Raw material not found: " +
                                            itemReq.getRawMaterialId()
                            )
                    );

            BigDecimal totalPrice = itemReq.getUnitPrice()
                    .multiply(itemReq.getQuantity());

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(po)
                    .rawMaterial(rawMaterial)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(totalPrice)
                    .receivedQuantity(BigDecimal.ZERO)
                    .build();

            purchaseOrderItemRepository.save(item);
            total = total.add(totalPrice);
        }

        po.setTotalAmount(total);
        purchaseOrderRepository.save(po);

        log.info("Purchase order updated: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse submitForApproval(Long id) {
        PurchaseOrder po = findPurchaseOrderById(id);
        validateStatus(po, PurchaseOrder.POStatus.DRAFT,
                "Only DRAFT orders can be submitted");
        po.setStatus(PurchaseOrder.POStatus.PENDING_APPROVAL);
        purchaseOrderRepository.save(po);
        log.info("PO submitted for approval: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse approvePurchaseOrder(
            Long id, String approvedByEmail) {
        PurchaseOrder po = findPurchaseOrderById(id);
        validateStatus(po, PurchaseOrder.POStatus.PENDING_APPROVAL,
                "Only PENDING_APPROVAL orders can be approved");

        User approvedBy = userRepository
                .findByEmailAndIsDeletedFalse(approvedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        po.setStatus(PurchaseOrder.POStatus.APPROVED);
        po.setApprovedBy(approvedBy);
        purchaseOrderRepository.save(po);
        log.info("PO approved: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse rejectPurchaseOrder(Long id) {
        PurchaseOrder po = findPurchaseOrderById(id);
        validateStatus(po, PurchaseOrder.POStatus.PENDING_APPROVAL,
                "Only PENDING_APPROVAL orders can be rejected");
        po.setStatus(PurchaseOrder.POStatus.REJECTED);
        purchaseOrderRepository.save(po);
        log.info("PO rejected: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse markAsOrdered(Long id) {
        PurchaseOrder po = findPurchaseOrderById(id);
        validateStatus(po, PurchaseOrder.POStatus.APPROVED,
                "Only APPROVED orders can be marked as ordered");
        po.setStatus(PurchaseOrder.POStatus.ORDERED);
        purchaseOrderRepository.save(po);
        log.info("PO marked as ordered: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse cancelPurchaseOrder(Long id) {
        PurchaseOrder po = findPurchaseOrderById(id);

        if (po.getStatus() == PurchaseOrder.POStatus.RECEIVED ||
                po.getStatus() == PurchaseOrder.POStatus.CANCELLED ||
                po.getStatus() == PurchaseOrder.POStatus.PARTIALLY_RECEIVED) {
            throw new IllegalArgumentException(
                    "Cannot cancel a RECEIVED or Partially received or already CANCELLED order"
            );
        }

        po.setStatus(PurchaseOrder.POStatus.CANCELLED);
        purchaseOrderRepository.save(po);
        log.info("PO cancelled: {}", po.getPoNumber());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePayment(
            Long id, PurchaseOrderPaymentRequest request) {

        PurchaseOrder po = findPurchaseOrderById(id);

        if (request.getPaidAmount().compareTo(po.getTotalAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Paid amount cannot exceed total amount of " +
                            po.getTotalAmount()
            );
        }

        po.setPaidAmount(request.getPaidAmount());
        po.setPaymentStatus(PurchaseOrder.PaymentStatus
                .valueOf(request.getPaymentStatus()));
        purchaseOrderRepository.save(po);

        log.info("PO payment updated: {} - {}",
                po.getPoNumber(), request.getPaymentStatus());
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = findPurchaseOrderById(id);

        if (po.getStatus() != PurchaseOrder.POStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT purchase orders can be deleted"
            );
        }

        po.setIsDeleted(true);
        po.setDeletedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);
        log.info("PO deleted: {}", po.getPoNumber());
    }

    @Override
    public String getNextPoNumber() {
        return purchaseOrderRepository.findLastPoNumber()
                .map(last -> {
                    int lastNumber = Integer.parseInt(
                            last.replace("PO-", "")
                    );
                    return String.format("PO-%03d", lastNumber + 1);
                })
                .orElse("PO-001");
    }

    // HELPERS
    private void validateStatus(PurchaseOrder po,
                                PurchaseOrder.POStatus required, String message) {
        if (po.getStatus() != required) {
            throw new IllegalArgumentException(message);
        }
    }

    private PurchaseOrder findPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Purchase order not found with id: " + id
                        )
                );
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder po) {
        List<PurchaseOrderItemResponse> items =
                purchaseOrderItemRepository
                        .findByPurchaseOrderId(po.getId())
                        .stream()
                        .map(this::mapItemToResponse)
                        .collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier().getId())
                .supplierCode(po.getSupplier().getCode())
                .supplierName(po.getSupplier().getName())
                .createdByName(po.getCreatedBy().getFullName())
                .approvedByName(po.getApprovedBy() != null
                        ? po.getApprovedBy().getFullName() : null)
                .totalAmount(po.getTotalAmount())
                .currency(po.getCurrency())
                .status(po.getStatus().name())
                .paymentStatus(po.getPaymentStatus().name())
                .paidAmount(po.getPaidAmount())
                .expectedDate(po.getExpectedDate())
                .notes(po.getNotes())
                .items(items)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private PurchaseOrderItemResponse mapItemToResponse(
            PurchaseOrderItem item) {
        return PurchaseOrderItemResponse.builder()
                .id(item.getId())
                .rawMaterialId(item.getRawMaterial().getId())
                .rawMaterialName(item.getRawMaterial().getName())
                .unit(item.getRawMaterial().getUnit().name())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .receivedQuantity(item.getReceivedQuantity())
                .build();
    }
}