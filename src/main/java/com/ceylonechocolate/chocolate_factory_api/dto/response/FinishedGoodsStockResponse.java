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
public class FinishedGoodsStockResponse {

    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal quantity;
    private LocalDateTime lastUpdated;
}