package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductionOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionOrderResponse;

import java.util.List;

public interface ProductionOrderService {

    List<ProductionOrderResponse> getAllOrders();

    List<ProductionOrderResponse> getOrdersByStatus(String status);

    ProductionOrderResponse getOrderById(Long id);

    ProductionOrderResponse createOrder(
            ProductionOrderRequest request, String createdByEmail);

    ProductionOrderResponse updateOrder(
            Long id, ProductionOrderRequest request);

    ProductionOrderResponse submitForApproval(Long id);

    ProductionOrderResponse approveOrder(Long id, String approvedByEmail);

    ProductionOrderResponse rejectOrder(Long id);

    ProductionOrderResponse cancelOrder(Long id);

    void deleteOrder(Long id);

    String getNextOrderNumber();
}