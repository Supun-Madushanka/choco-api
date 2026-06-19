package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<PurchaseOrder> findByIdAndIsDeletedFalse(Long id);

    List<PurchaseOrder> findBySupplierIdAndIsDeletedFalse(Long supplierId);

    List<PurchaseOrder> findByStatusAndIsDeletedFalse(
            PurchaseOrder.POStatus status);

    @Query("SELECT p.poNumber FROM PurchaseOrder p " +
            "ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLastPoNumber();
}