package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository
        extends JpaRepository<ProductionOrder, Long> {

    List<ProductionOrder> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<ProductionOrder> findByIdAndIsDeletedFalse(Long id);

    List<ProductionOrder> findByStatusAndIsDeletedFalse(
            ProductionOrder.POStatus status);

    @Query("SELECT p.orderNumber FROM ProductionOrder p " +
            "ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLastOrderNumber();
}