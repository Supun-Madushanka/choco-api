package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.WarehouseRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    List<WarehouseResponse> getAllWarehouses();

    List<WarehouseResponse> getActiveWarehouses();

    WarehouseResponse getWarehouseById(Long id);

    WarehouseResponse createWarehouse(WarehouseRequest request);

    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    void deleteWarehouse(Long id);
}