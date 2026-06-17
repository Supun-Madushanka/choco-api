package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByWarehouseIdOrderByCreatedAtDesc(
            Long warehouseId);

    List<StockMovement> findByRawMaterialIdOrderByCreatedAtDesc(
            Long rawMaterialId);

    List<StockMovement> findByWarehouseIdAndRawMaterialIdOrderByCreatedAtDesc(
            Long warehouseId, Long rawMaterialId);

    List<StockMovement> findByMovementTypeOrderByCreatedAtDesc(
            StockMovement.MovementType movementType);
}