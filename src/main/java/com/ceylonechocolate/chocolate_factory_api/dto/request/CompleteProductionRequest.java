package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteProductionRequest {

    @NotNull(message = "Quantity produced is required")
    @DecimalMin(value = "0.01", message = "Quantity produced must be greater than 0")
    private BigDecimal quantityProduced;

    @DecimalMin(value = "0.0", message = "Quantity rejected cannot be negative")
    private BigDecimal quantityRejected = BigDecimal.ZERO;

    @NotNull(message = "Production date is required")
    private LocalDate productionDate;

    private LocalDate expiryDate;

    @NotEmpty(message = "Raw material consumption details are required")
    private List<BatchConsumptionItemRequest> consumptions;
}