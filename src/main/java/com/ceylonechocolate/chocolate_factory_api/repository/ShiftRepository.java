package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository
        extends JpaRepository<Shift, Long> {

    boolean existsByName(String name);

    List<Shift> findByIsActiveTrue();
}