package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsReceivedNoteRequest {

    @NotNull(message = "Purchase order is required")
    private Long purchaseOrderId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    @NotNull(message = "Received date is required")
    private LocalDate receivedDate;

    private String notes;

    @NotEmpty(message = "At least one item is required")
    private List<GrnItemCreateRequest> items;
}