package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentRequest {

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Shift is required")
    private Long shiftId;

    @NotNull(message = "Assigned date is required")
    private LocalDate assignedDate;

    private String note;
}