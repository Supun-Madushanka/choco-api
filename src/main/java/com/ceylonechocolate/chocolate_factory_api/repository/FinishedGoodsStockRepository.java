package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.FinishedGoodsStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinishedGoodsStockRepository
        extends JpaRepository<FinishedGoodsStock, Long> {

    Optional<FinishedGoodsStock> findByProductIdAndWarehouseId(
            Long productId, Long warehouseId);

    List<FinishedGoodsStock> findByWarehouseId(Long warehouseId);

    List<FinishedGoodsStock> findByProductId(Long productId);
}