package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseStockResponse;

import java.util.List;

public interface WarehouseStockService {

    List<WarehouseStockResponse> getAllStock();

    List<WarehouseStockResponse> getStockByWarehouse(Long warehouseId);

    List<WarehouseStockResponse> getStockByRawMaterial(Long rawMaterialId);

    List<WarehouseStockResponse> getLowStockItems();

    WarehouseStockResponse getStockByWarehouseAndMaterial(
            Long warehouseId, Long rawMaterialId);
}