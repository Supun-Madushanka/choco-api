package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollResponse {

    private Long id;
    private Long employeeId;
    private String employeeNo;
    private String employeeName;
    private String departmentName;
    private Integer month;
    private Integer year;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal epfEmployee;
    private BigDecimal etfEmployer;
    private BigDecimal tax;
    private BigDecimal additionalDeductions;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private String processedByName;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}