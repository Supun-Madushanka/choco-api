package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineResponse {

    private Long id;
    private String code;
    private String name;
    private String model;
    private String serialNo;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}