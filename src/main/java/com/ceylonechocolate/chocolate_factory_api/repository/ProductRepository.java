package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String name);

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    List<Product> findByIsDeletedFalse();

    List<Product> findByIsActiveTrueAndIsDeletedFalse();

    List<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId);

    Optional<Product> findTopByCategoryIdOrderByIdDesc(Long categoryId);
}