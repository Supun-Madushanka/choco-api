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
public class RawMaterialResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String unit;
    private BigDecimal minStockLevel;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}