package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.RawMaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialCategoryRepository
        extends JpaRepository<RawMaterialCategory, Long> {

    boolean existsByName(String name);
}