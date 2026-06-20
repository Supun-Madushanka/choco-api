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
public class ProductionOrderResponse {

    private Long id;
    private String orderNumber;
    private Long productId;
    private String productCode;
    private String productName;
    private String createdByName;
    private String approvedByName;
    private BigDecimal plannedQuantity;
    private BigDecimal actualQuantity;
    private LocalDate plannedDate;
    private LocalDate actualDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}