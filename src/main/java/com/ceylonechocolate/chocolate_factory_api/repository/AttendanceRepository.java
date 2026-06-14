package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndWorkDate(
            Long employeeId, LocalDate workDate);

    boolean existsByEmployeeIdAndWorkDate(
            Long employeeId, LocalDate workDate);

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByWorkDate(LocalDate workDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetween(
            Long employeeId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetweenOrderByWorkDateDesc(
            Long employeeId, LocalDate startDate, LocalDate endDate);
}