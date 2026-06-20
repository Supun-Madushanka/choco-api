package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.response.FinishedGoodsStockResponse;

import java.util.List;

public interface FinishedGoodsStockService {

    List<FinishedGoodsStockResponse> getAllStock();

    List<FinishedGoodsStockResponse> getStockByWarehouse(Long warehouseId);

    List<FinishedGoodsStockResponse> getStockByProduct(Long productId);
}