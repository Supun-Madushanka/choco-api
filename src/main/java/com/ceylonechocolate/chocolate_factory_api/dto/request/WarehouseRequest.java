package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal capacity;
    private String description;
    private Boolean isActive = true;
}