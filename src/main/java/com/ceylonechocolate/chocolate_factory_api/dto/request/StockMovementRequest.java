package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class StockMovementRequest {

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    @NotNull(message = "Raw material is required")
    private Long rawMaterialId;

    @NotBlank(message = "Movement type is required")
    private String movementType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    private Long referenceId;
    private String referenceType;
    private String note;
}