package com.ceylonechocolate.chocolate_factory_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftUpdateRequest {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private String type;
    private String description;
    private Boolean isActive;
}
