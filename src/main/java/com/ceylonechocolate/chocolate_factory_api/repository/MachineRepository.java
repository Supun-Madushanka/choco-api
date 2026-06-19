package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    boolean existsByCode(String code);

    boolean existsBySerialNo(String serialNo);

    Optional<Machine> findByIdAndIsDeletedFalse(Long id);

    List<Machine> findByIsDeletedFalse();

    List<Machine> findByStatusAndIsDeletedFalse(
            Machine.MachineStatus status);

    @Query("SELECT m.code FROM Machine m ORDER BY m.id DESC LIMIT 1")
    Optional<String> findLastCode();
}