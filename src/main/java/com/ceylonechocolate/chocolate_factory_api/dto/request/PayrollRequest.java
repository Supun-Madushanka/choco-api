package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollRequest {

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Invalid year")
    private Integer year;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Basic salary must be greater than 0")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", message = "Allowances cannot be negative")
    private BigDecimal allowances = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Additional deductions cannot be negative")
    private BigDecimal additionalDeductions = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax percentage cannot exceed 100")
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    private String note;
}