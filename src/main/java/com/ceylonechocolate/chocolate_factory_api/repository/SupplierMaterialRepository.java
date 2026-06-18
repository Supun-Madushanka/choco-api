package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.SupplierMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierMaterialRepository
        extends JpaRepository<SupplierMaterial, Long> {

    List<SupplierMaterial> findBySupplierId(Long supplierId);

    List<SupplierMaterial> findByRawMaterialId(Long rawMaterialId);

    Optional<SupplierMaterial> findBySupplierIdAndRawMaterialId(
            Long supplierId, Long rawMaterialId);

    boolean existsBySupplierIdAndRawMaterialId(
            Long supplierId, Long rawMaterialId);

    List<SupplierMaterial> findByRawMaterialIdAndIsPreferredTrue(
            Long rawMaterialId);
}