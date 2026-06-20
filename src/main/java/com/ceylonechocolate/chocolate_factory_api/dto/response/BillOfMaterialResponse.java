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
public class BillOfMaterialResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialUnit;
    private BigDecimal quantityRequired;
    private String unit;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}