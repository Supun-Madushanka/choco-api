package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.response.FinishedGoodsStockResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.FinishedGoodsStock;
import com.ceylonechocolate.chocolate_factory_api.repository.FinishedGoodsStockRepository;
import com.ceylonechocolate.chocolate_factory_api.service.FinishedGoodsStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinishedGoodsStockServiceImpl
        implements FinishedGoodsStockService {

    private final FinishedGoodsStockRepository finishedGoodsStockRepository;

    @Override
    public List<FinishedGoodsStockResponse> getAllStock() {
        return finishedGoodsStockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinishedGoodsStockResponse> getStockByWarehouse(
            Long warehouseId) {
        return finishedGoodsStockRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinishedGoodsStockResponse> getStockByProduct(
            Long productId) {
        return finishedGoodsStockRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FinishedGoodsStockResponse mapToResponse(
            FinishedGoodsStock stock) {
        return FinishedGoodsStockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProduct().getId())
                .productCode(stock.getProduct().getCode())
                .productName(stock.getProduct().getName())
                .unit(stock.getProduct().getUnit().name())
                .warehouseId(stock.getWarehouse().getId())
                .warehouseName(stock.getWarehouse().getName())
                .quantity(stock.getQuantity())
                .lastUpdated(stock.getLastUpdated())
                .build();
    }
}