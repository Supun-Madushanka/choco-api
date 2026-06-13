package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeNo(String employeeNo);

    boolean existsByNic(String nic);

    Optional<Employee> findByEmployeeNo(String employeeNo);

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByIsDeletedFalse();

    List<Employee> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    boolean existsByNicAndIsDeletedFalse(String nic);

    boolean existsByEmployeeNoAndIsDeletedFalse(String employeeNo);
}