package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequest {

    @NotBlank(message = "Machine name is required")
    private String name;

    private String model;
    private String serialNo;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private String status;
}