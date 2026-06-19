package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.GoodsReceivedNoteRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.GrnItemInspectRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.GoodsReceivedNoteResponse;

import java.util.List;

public interface GoodsReceivedNoteService {

    List<GoodsReceivedNoteResponse> getAllGrns();

    GoodsReceivedNoteResponse getGrnById(Long id);

    List<GoodsReceivedNoteResponse> getGrnsByPurchaseOrder(
            Long purchaseOrderId);

    GoodsReceivedNoteResponse createGrn(
            GoodsReceivedNoteRequest request, String receivedByEmail);

    GoodsReceivedNoteResponse submitForQc(Long id);

    GoodsReceivedNoteResponse inspectItem(
            Long grnId, Long itemId,
            GrnItemInspectRequest request, String inspectedByEmail);

    GoodsReceivedNoteResponse completeQc(Long id, String movedByEmail);

    String getNextGrnNumber();
}