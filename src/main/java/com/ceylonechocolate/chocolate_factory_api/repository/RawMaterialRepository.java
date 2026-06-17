package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawMaterialRepository
        extends JpaRepository<RawMaterial, Long> {

    boolean existsByName(String name);

    List<RawMaterial> findByIsActiveTrue();

    List<RawMaterial> findByCategoryId(Long categoryId);
}