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
public class SupplierMaterialResponse {

    private Long id;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String unit;
    private BigDecimal unitPrice;
    private String currency;
    private Integer leadTimeDays;
    private Boolean isPreferred;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}