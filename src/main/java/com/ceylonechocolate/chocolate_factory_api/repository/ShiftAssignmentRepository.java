package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftAssignmentRepository
        extends JpaRepository<ShiftAssignment, Long> {

    List<ShiftAssignment> findByEmployeeId(Long employeeId);

    List<ShiftAssignment> findByAssignedDate(LocalDate assignedDate);

    List<ShiftAssignment> findByEmployeeIdAndAssignedDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<ShiftAssignment> findByEmployeeIdAndAssignedDate(
            Long employeeId,
            LocalDate assignedDate
    );

    boolean existsByEmployeeIdAndAssignedDate(
            Long employeeId,
            LocalDate assignedDate
    );
}