package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionBatchCreateRequest {

    @NotNull(message = "Production order is required")
    private Long productionOrderId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    private String notes;
}