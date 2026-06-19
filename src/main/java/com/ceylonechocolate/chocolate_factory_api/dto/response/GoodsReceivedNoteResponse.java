package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsReceivedNoteResponse {

    private Long id;
    private String grnNumber;
    private Long purchaseOrderId;
    private String poNumber;
    private Long warehouseId;
    private String warehouseName;
    private String receivedByName;
    private LocalDate receivedDate;
    private String status;
    private String notes;
    private List<GrnItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}