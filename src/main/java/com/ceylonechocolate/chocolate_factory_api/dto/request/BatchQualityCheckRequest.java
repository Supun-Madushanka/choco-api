package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchQualityCheckRequest {

    @NotBlank(message = "Check type is required")
    private String checkType;

    @NotBlank(message = "Result is required")
    private String result;

    private String notes;
}