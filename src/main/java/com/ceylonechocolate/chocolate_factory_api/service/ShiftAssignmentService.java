package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftAssignmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftAssignmentResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentService {

    ShiftAssignmentResponse assignShift(
            ShiftAssignmentRequest request,
            String assignedByEmail
    );

    List<ShiftAssignmentResponse> getAssignmentsByEmployee(Long employeeId);

    List<ShiftAssignmentResponse> getAssignmentsByDate(LocalDate date);

    List<ShiftAssignmentResponse> getAssignmentsByEmployeeAndDateRange(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    void deleteAssignment(Long id);
}