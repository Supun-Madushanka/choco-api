package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseStockResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.WarehouseStock;
import com.ceylonechocolate.chocolate_factory_api.repository.WarehouseStockRepository;
import com.ceylonechocolate.chocolate_factory_api.service.WarehouseStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseStockServiceImpl implements WarehouseStockService {

    private final WarehouseStockRepository warehouseStockRepository;

    @Override
    public List<WarehouseStockResponse> getAllStock() {
        return warehouseStockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseStockResponse> getStockByWarehouse(
            Long warehouseId) {
        return warehouseStockRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseStockResponse> getStockByRawMaterial(
            Long rawMaterialId) {
        return warehouseStockRepository.findByRawMaterialId(rawMaterialId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseStockResponse> getLowStockItems() {
        return warehouseStockRepository.findLowStockItems()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseStockResponse getStockByWarehouseAndMaterial(
            Long warehouseId, Long rawMaterialId) {
        WarehouseStock stock = warehouseStockRepository
                .findByWarehouseIdAndRawMaterialId(warehouseId, rawMaterialId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Stock record not found"
                        )
                );
        return mapToResponse(stock);
    }

    private WarehouseStockResponse mapToResponse(WarehouseStock stock) {
        boolean isLowStock = stock.getQuantity()
                .compareTo(stock.getRawMaterial().getMinStockLevel()) < 0;

        return WarehouseStockResponse.builder()
                .id(stock.getId())
                .warehouseId(stock.getWarehouse().getId())
                .warehouseName(stock.getWarehouse().getName())
                .rawMaterialId(stock.getRawMaterial().getId())
                .rawMaterialName(stock.getRawMaterial().getName())
                .categoryName(stock.getRawMaterial().getCategory().getName())
                .unit(stock.getRawMaterial().getUnit().name())
                .quantity(stock.getQuantity())
                .minStockLevel(stock.getRawMaterial().getMinStockLevel())
                .isLowStock(isLowStock)
                .lastUpdated(stock.getLastUpdated())
                .build();
    }
}