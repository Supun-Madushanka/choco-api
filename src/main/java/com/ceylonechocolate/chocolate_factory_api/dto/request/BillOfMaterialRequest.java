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
public class BillOfMaterialRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Raw material is required")
    private Long rawMaterialId;

    @NotNull(message = "Quantity required is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    private BigDecimal quantityRequired;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String notes;
}