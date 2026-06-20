package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.ProductionBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionBatchRepository
        extends JpaRepository<ProductionBatch, Long> {

    List<ProductionBatch> findAllByOrderByCreatedAtDesc();

    List<ProductionBatch> findByProductionOrderId(Long productionOrderId);

    List<ProductionBatch> findByStatus(ProductionBatch.BatchStatus status);

    @Query("SELECT b.batchNumber FROM ProductionBatch b " +
            "ORDER BY b.id DESC LIMIT 1")
    Optional<String> findLastBatchNumber();

    @Query("SELECT COALESCE(SUM(b.quantityProduced), 0) " +
            "FROM ProductionBatch b " +
            "WHERE b.productionOrder.id = :orderId " +
            "AND b.status = 'STOCKED'")
    BigDecimal getTotalStockedQuantityByOrder(Long orderId);
}