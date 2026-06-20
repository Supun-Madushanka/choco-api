package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchConsumptionItemRequest {

    @NotNull(message = "Raw material is required")
    private Long rawMaterialId;

    @NotNull(message = "Quantity consumed is required")
    @DecimalMin(value = "0.0", message = "Quantity consumed cannot be negative")
    private BigDecimal quantityConsumed;
}