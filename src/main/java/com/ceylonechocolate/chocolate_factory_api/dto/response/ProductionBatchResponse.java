package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionBatchResponse {

    private Long id;
    private String batchNumber;
    private Long productionOrderId;
    private String orderNumber;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal quantityProduced;
    private BigDecimal quantityRejected;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private String qcStatus;
    private String qcMarkedByName;
    private LocalDateTime qcMarkedAt;
    private String finalStatus;
    private String finalApprovedByName;
    private LocalDateTime finalApprovedAt;
    private String status;
    private String supervisedByName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}