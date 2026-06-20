package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchQualityCheckResponse {

    private Long id;
    private Long productionBatchId;
    private String batchNumber;
    private String checkType;
    private String result;
    private String checkedByName;
    private String notes;
    private LocalDateTime checkedAt;
}