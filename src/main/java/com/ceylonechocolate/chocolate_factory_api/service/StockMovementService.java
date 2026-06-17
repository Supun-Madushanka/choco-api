package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.StockMovementRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.StockMovementResponse;

import java.util.List;

public interface StockMovementService {

    StockMovementResponse recordMovement(
            StockMovementRequest request, String movedByEmail);

    List<StockMovementResponse> getAllMovements();

    List<StockMovementResponse> getMovementsByWarehouse(Long warehouseId);

    List<StockMovementResponse> getMovementsByRawMaterial(Long rawMaterialId);

    List<StockMovementResponse> getMovementsByWarehouseAndMaterial(
            Long warehouseId, Long rawMaterialId);

    List<StockMovementResponse> getMovementsByType(String movementType);
}