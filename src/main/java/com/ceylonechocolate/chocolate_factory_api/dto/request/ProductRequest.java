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
public class ProductRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    private String name;

    private String variant;
    private String packagingType;

    @NotBlank(message = "Unit is required")
    private String unit;

    private BigDecimal weightPerUnit;

    @NotNull(message = "Selling price is required")
    private BigDecimal sellingPrice;

    private String description;

    private Boolean isActive = true;
}