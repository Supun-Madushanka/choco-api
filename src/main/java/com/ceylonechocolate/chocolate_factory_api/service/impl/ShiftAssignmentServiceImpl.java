package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftAssignmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftAssignmentResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Employee;
import com.ceylonechocolate.chocolate_factory_api.entity.Shift;
import com.ceylonechocolate.chocolate_factory_api.entity.ShiftAssignment;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.EmployeeRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ShiftAssignmentRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ShiftRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.ShiftAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShiftAssignmentResponse assignShift(
            ShiftAssignmentRequest request,
            String assignedByEmail) {

        // Check if already assigned for this date
        if (shiftAssignmentRepository.existsByEmployeeIdAndAssignedDate(
                request.getEmployeeId(), request.getAssignedDate())) {
            throw new IllegalArgumentException(
                    "Employee already has a shift assigned for this date"
            );
        }

        // Load employee
        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found")
                );

        // Load shift
        Shift shift = shiftRepository
                .findById(request.getShiftId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Shift not found")
                );

        // Load assigning user
        User assignedBy = userRepository
                .findByEmailAndIsDeletedFalse(assignedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        ShiftAssignment assignment = ShiftAssignment.builder()
                .employee(employee)
                .shift(shift)
                .assignedDate(request.getAssignedDate())
                .assignedBy(assignedBy)
                .note(request.getNote())
                .build();

        shiftAssignmentRepository.save(assignment);
        log.info("Shift assigned to employee: {}",
                employee.getEmployeeNo());

        return mapToResponse(assignment);
    }

    @Override
    public List<ShiftAssignmentResponse> getAssignmentsByEmployee(
            Long employeeId) {
        return shiftAssignmentRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftAssignmentResponse> getAssignmentsByDate(
            LocalDate date) {
        return shiftAssignmentRepository
                .findByAssignedDate(date)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftAssignmentResponse> getAssignmentsByEmployeeAndDateRange(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {
        return shiftAssignmentRepository
                .findByEmployeeIdAndAssignedDateBetween(
                        employeeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        ShiftAssignment assignment = shiftAssignmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shift assignment not found"
                        )
                );
        shiftAssignmentRepository.delete(assignment);
        log.info("Shift assignment deleted: {}", id);
    }

    private ShiftAssignmentResponse mapToResponse(ShiftAssignment a) {
        return ShiftAssignmentResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .employeeNo(a.getEmployee().getEmployeeNo())
                .employeeName(a.getEmployee().getFullName())
                .shiftId(a.getShift().getId())
                .shiftName(a.getShift().getName())
                .shiftStartTime(a.getShift().getStartTime())
                .shiftEndTime(a.getShift().getEndTime())
                .assignedDate(a.getAssignedDate())
                .assignedByName(a.getAssignedBy().getFullName())
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .build();
    }
}