package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseStockRepository
        extends JpaRepository<WarehouseStock, Long> {

    Optional<WarehouseStock> findByWarehouseIdAndRawMaterialId(
            Long warehouseId, Long rawMaterialId);

    List<WarehouseStock> findByWarehouseId(Long warehouseId);

    List<WarehouseStock> findByRawMaterialId(Long rawMaterialId);

    // Total stock across all warehouses for a material
    @Query("SELECT COALESCE(SUM(ws.quantity), 0) " +
            "FROM WarehouseStock ws " +
            "WHERE ws.rawMaterial.id = :rawMaterialId")
    java.math.BigDecimal getTotalStockByRawMaterialId(Long rawMaterialId);

    // Low stock materials (total quantity < min stock level)
    @Query("SELECT ws FROM WarehouseStock ws " +
            "WHERE ws.quantity < ws.rawMaterial.minStockLevel")
    List<WarehouseStock> findLowStockItems();
}