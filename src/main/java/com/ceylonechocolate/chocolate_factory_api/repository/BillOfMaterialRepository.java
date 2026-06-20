package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillOfMaterialRepository
        extends JpaRepository<BillOfMaterial, Long> {

    List<BillOfMaterial> findByProductId(Long productId);

    Optional<BillOfMaterial> findByProductIdAndRawMaterialId(
            Long productId, Long rawMaterialId);

    boolean existsByProductIdAndRawMaterialId(
            Long productId, Long rawMaterialId);

    List<BillOfMaterial> findByRawMaterialId(Long rawMaterialId);
}