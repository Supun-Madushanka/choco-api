package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrderRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Planned quantity is required")
    @DecimalMin(value = "0.01", message = "Planned quantity must be greater than 0")
    private BigDecimal plannedQuantity;

    @NotNull(message = "Planned date is required")
    private LocalDate plannedDate;

    private String notes;
}