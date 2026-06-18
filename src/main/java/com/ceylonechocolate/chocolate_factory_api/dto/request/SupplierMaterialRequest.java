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
public class SupplierMaterialRequest {

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @NotNull(message = "Raw material is required")
    private Long rawMaterialId;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    private String currency = "LKR";

    private Integer leadTimeDays;

    private Boolean isPreferred = false;
}