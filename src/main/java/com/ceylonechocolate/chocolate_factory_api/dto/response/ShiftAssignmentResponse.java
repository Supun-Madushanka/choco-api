package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentResponse {

    private Long id;
    private Long employeeId;
    private String employeeNo;
    private String employeeName;
    private Long shiftId;
    private String shiftName;
    private LocalTime shiftStartTime;
    private LocalTime shiftEndTime;
    private LocalDate assignedDate;
    private String assignedByName;
    private String note;
    private LocalDateTime createdAt;
}