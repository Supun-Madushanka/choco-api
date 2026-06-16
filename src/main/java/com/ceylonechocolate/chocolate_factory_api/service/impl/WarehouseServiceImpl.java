package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.WarehouseRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.WarehouseResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Warehouse;
import com.ceylonechocolate.chocolate_factory_api.repository.WarehouseRepository;
import com.ceylonechocolate.chocolate_factory_api.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseResponse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        return mapToResponse(findWarehouseById(id));
    }

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {

        if (warehouseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Warehouse already exists with name: " + request.getName()
            );
        }

        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null
                        ? request.getIsActive() : true)
                .build();

        warehouseRepository.save(warehouse);
        log.info("Warehouse created: {}", warehouse.getName());

        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {

        Warehouse warehouse = findWarehouseById(id);

        if (!warehouse.getName().equals(request.getName()) &&
                warehouseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Warehouse already exists with name: " + request.getName()
            );
        }

        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setLatitude(request.getLatitude());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            warehouse.setIsActive(request.getIsActive());
        }

        warehouseRepository.save(warehouse);
        log.info("Warehouse updated: {}", warehouse.getName());

        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = findWarehouseById(id);
        warehouseRepository.delete(warehouse);
        log.info("Warehouse deleted: {}", warehouse.getName());
    }

    private Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Warehouse not found with id: " + id
                        )
                );
    }

    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .capacity(warehouse.getCapacity())
                .description(warehouse.getDescription())
                .isActive(warehouse.getIsActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}