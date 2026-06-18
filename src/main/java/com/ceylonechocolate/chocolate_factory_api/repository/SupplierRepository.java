package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String name);

    Optional<Supplier> findByIdAndIsDeletedFalse(Long id);

    List<Supplier> findByIsDeletedFalse();

    List<Supplier> findByStatusAndIsDeletedFalse(
            Supplier.SupplierStatus status);

    List<Supplier> findBySupplierTypeAndIsDeletedFalse(
            Supplier.SupplierType supplierType);

    long countByIsDeletedFalse();

    @Query("SELECT s.code FROM Supplier s ORDER BY s.id DESC LIMIT 1")
    Optional<String> findLastCode();
}