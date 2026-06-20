package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.*;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductionBatchResponse;

import java.util.List;

public interface ProductionBatchService {

    List<ProductionBatchResponse> getAllBatches();

    List<ProductionBatchResponse> getBatchesByOrder(Long productionOrderId);

    ProductionBatchResponse getBatchById(Long id);

    ProductionBatchResponse createBatch(
            ProductionBatchCreateRequest request, String supervisedByEmail);

    ProductionBatchResponse completeProduction(
            Long id, CompleteProductionRequest request, String movedByEmail);

    ProductionBatchResponse markQc(
            Long id, BatchQcRequest request, String qcMarkedByEmail);

    ProductionBatchResponse finalApprove(
            Long id, BatchFinalApprovalRequest request,
            String approvedByEmail);

    String getNextBatchNumber();
}