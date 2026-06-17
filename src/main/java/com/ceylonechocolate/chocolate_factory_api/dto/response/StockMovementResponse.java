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
public class StockMovementResponse {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String unit;
    private String movementType;
    private BigDecimal quantity;
    private BigDecimal stockAfterMovement;
    private Long referenceId;
    private String referenceType;
    private String note;
    private String movedByName;
    private LocalDateTime createdAt;
}