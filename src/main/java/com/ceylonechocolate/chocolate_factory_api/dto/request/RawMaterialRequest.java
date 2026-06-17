package com.ceylonechocolate.chocolate_factory_api.dto.request;

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
public class RawMaterialRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Material name is required")
    private String name;

    @NotBlank(message = "Unit is required")
    private String unit;

    private BigDecimal minStockLevel = BigDecimal.ZERO;

    private String description;

    private Boolean isActive = true;
}