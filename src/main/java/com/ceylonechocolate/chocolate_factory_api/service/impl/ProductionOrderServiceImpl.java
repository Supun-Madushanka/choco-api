package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductionOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionOrderResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Product;
import com.ceylonechocolate.chocolate_factory_api.entity.ProductionOrder;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductionOrderRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.ProductionOrderService;
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
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProductionOrderResponse> getAllOrders() {
        return productionOrderRepository
                .findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrderResponse> getOrdersByStatus(String status) {
        return productionOrderRepository
                .findByStatusAndIsDeletedFalse(
                        ProductionOrder.POStatus.valueOf(status))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductionOrderResponse getOrderById(Long id) {
        return mapToResponse(findOrderById(id));
    }

    @Override
    @Transactional
    public ProductionOrderResponse createOrder(
            ProductionOrderRequest request, String createdByEmail) {

        Product product = productRepository
                .findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found")
                );

        User createdBy = userRepository
                .findByEmailAndIsDeletedFalse(createdByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String orderNumber = getNextOrderNumber();

        ProductionOrder order = ProductionOrder.builder()
                .orderNumber(orderNumber)
                .product(product)
                .createdBy(createdBy)
                .plannedQuantity(request.getPlannedQuantity())
                .actualQuantity(BigDecimal.ZERO)
                .plannedDate(request.getPlannedDate())
                .status(ProductionOrder.POStatus.DRAFT)
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        productionOrderRepository.save(order);
        log.info("Production order created: {}", orderNumber);

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponse updateOrder(
            Long id, ProductionOrderRequest request) {

        ProductionOrder order = findOrderById(id);

        if (order.getStatus() != ProductionOrder.POStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT production orders can be updated"
            );
        }

        Product product = productRepository
                .findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found")
                );

        order.setProduct(product);
        order.setPlannedQuantity(request.getPlannedQuantity());
        order.setPlannedDate(request.getPlannedDate());
        order.setNotes(request.getNotes());

        productionOrderRepository.save(order);
        log.info("Production order updated: {}", order.getOrderNumber());

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponse submitForApproval(Long id) {
        ProductionOrder order = findOrderById(id);
        validateStatus(order, ProductionOrder.POStatus.DRAFT,
                "Only DRAFT orders can be submitted");
        order.setStatus(ProductionOrder.POStatus.PENDING_APPROVAL);
        productionOrderRepository.save(order);
        log.info("Production order submitted for approval: {}",
                order.getOrderNumber());
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponse approveOrder(
            Long id, String approvedByEmail) {
        ProductionOrder order = findOrderById(id);
        validateStatus(order, ProductionOrder.POStatus.PENDING_APPROVAL,
                "Only PENDING_APPROVAL orders can be approved");

        User approvedBy = userRepository
                .findByEmailAndIsDeletedFalse(approvedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        order.setStatus(ProductionOrder.POStatus.APPROVED);
        order.setApprovedBy(approvedBy);
        productionOrderRepository.save(order);
        log.info("Production order approved: {}", order.getOrderNumber());
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponse rejectOrder(Long id) {
        ProductionOrder order = findOrderById(id);
        validateStatus(order, ProductionOrder.POStatus.PENDING_APPROVAL,
                "Only PENDING_APPROVAL orders can be rejected");
        order.setStatus(ProductionOrder.POStatus.CANCELLED);
        productionOrderRepository.save(order);
        log.info("Production order rejected: {}", order.getOrderNumber());
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponse cancelOrder(Long id) {
        ProductionOrder order = findOrderById(id);

        if (order.getStatus() == ProductionOrder.POStatus.COMPLETED ||
                order.getStatus() == ProductionOrder.POStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot cancel a COMPLETED or already CANCELLED order"
            );
        }

        order.setStatus(ProductionOrder.POStatus.CANCELLED);
        productionOrderRepository.save(order);
        log.info("Production order cancelled: {}", order.getOrderNumber());
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        ProductionOrder order = findOrderById(id);

        if (order.getStatus() != ProductionOrder.POStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT production orders can be deleted"
            );
        }

        order.setIsDeleted(true);
        order.setDeletedAt(LocalDateTime.now());
        productionOrderRepository.save(order);
        log.info("Production order deleted: {}", order.getOrderNumber());
    }

    @Override
    public String getNextOrderNumber() {
        return productionOrderRepository.findLastOrderNumber()
                .map(last -> {
                    int lastNumber = Integer.parseInt(
                            last.replace("PRO-", "")
                    );
                    return String.format("PRO-%03d", lastNumber + 1);
                })
                .orElse("PRO-001");
    }

    // HELPERS
    private void validateStatus(ProductionOrder order,
                                ProductionOrder.POStatus required, String message) {
        if (order.getStatus() != required) {
            throw new IllegalArgumentException(message);
        }
    }

    private ProductionOrder findOrderById(Long id) {
        return productionOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Production order not found with id: " + id
                        )
                );
    }

    private ProductionOrderResponse mapToResponse(ProductionOrder order) {
        return ProductionOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .productId(order.getProduct().getId())
                .productCode(order.getProduct().getCode())
                .productName(order.getProduct().getName())
                .createdByName(order.getCreatedBy().getFullName())
                .approvedByName(order.getApprovedBy() != null
                        ? order.getApprovedBy().getFullName() : null)
                .plannedQuantity(order.getPlannedQuantity())
                .actualQuantity(order.getActualQuantity())
                .plannedDate(order.getPlannedDate())
                .actualDate(order.getActualDate())
                .status(order.getStatus().name())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}