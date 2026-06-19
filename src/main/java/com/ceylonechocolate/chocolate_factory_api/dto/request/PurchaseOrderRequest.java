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
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    private String currency = "LKR";

    private LocalDate expectedDate;

    private String notes;

    @NotEmpty(message = "At least one item is required")
    private List<PurchaseOrderItemRequest> items;
}