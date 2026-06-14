package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository
        extends JpaRepository<Payroll, Long> {

    boolean existsByEmployeeIdAndMonthAndYear(
            Long employeeId, Integer month, Integer year);

    Optional<Payroll> findByEmployeeIdAndMonthAndYear(
            Long employeeId, Integer month, Integer year);

    List<Payroll> findByEmployeeId(Long employeeId);

    List<Payroll> findByMonthAndYear(Integer month, Integer year);

    List<Payroll> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
}