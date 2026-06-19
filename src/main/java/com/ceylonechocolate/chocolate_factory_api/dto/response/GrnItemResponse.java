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
public class GrnItemResponse {

    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String unit;
    private BigDecimal orderedQuantity;
    private BigDecimal receivedQuantity;
    private BigDecimal acceptedQuantity;
    private BigDecimal rejectedQuantity;
    private String qualityStatus;
    private String qualityNotes;
    private String inspectedByName;
    private LocalDateTime inspectedAt;
}