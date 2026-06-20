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
public class MaintenanceLogResponse {

    private Long id;
    private Long machineId;
    private String machineCode;
    private String machineName;
    private String maintenanceType;
    private String description;
    private LocalDate maintenanceDate;
    private LocalDate nextMaintenanceDate;
    private BigDecimal cost;
    private String performedByName;
    private LocalDateTime createdAt;
}