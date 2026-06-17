package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockResponse {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String categoryName;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal minStockLevel;
    private boolean isLowStock;
    private LocalDateTime lastUpdated;
}