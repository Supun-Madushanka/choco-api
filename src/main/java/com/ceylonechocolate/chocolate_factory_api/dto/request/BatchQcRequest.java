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
public class BatchQcRequest {

    @NotBlank(message = "QC status is required")
    private String qcStatus;
}