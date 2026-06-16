package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository
        extends JpaRepository<Warehouse, Long> {

    boolean existsByName(String name);

    List<Warehouse> findByIsActiveTrue();
}