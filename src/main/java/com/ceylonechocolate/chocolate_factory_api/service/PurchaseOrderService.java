package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderPaymentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.PurchaseOrderRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PurchaseOrderResponse;

import java.util.List;

public interface PurchaseOrderService {

    List<PurchaseOrderResponse> getAllPurchaseOrders();

    List<PurchaseOrderResponse> getPurchaseOrdersByStatus(String status);

    PurchaseOrderResponse getPurchaseOrderById(Long id);

    PurchaseOrderResponse createPurchaseOrder(
            PurchaseOrderRequest request, String createdByEmail);

    PurchaseOrderResponse updatePurchaseOrder(
            Long id, PurchaseOrderRequest request);

    PurchaseOrderResponse submitForApproval(Long id);

    PurchaseOrderResponse approvePurchaseOrder(
            Long id, String approvedByEmail);

    PurchaseOrderResponse rejectPurchaseOrder(Long id);

    PurchaseOrderResponse markAsOrdered(Long id);

    PurchaseOrderResponse cancelPurchaseOrder(Long id);

    PurchaseOrderResponse updatePayment(
            Long id, PurchaseOrderPaymentRequest request);

    void deletePurchaseOrder(Long id);

    String getNextPoNumber();
}