package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BillOfMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BillOfMaterialResponse;

import java.util.List;

public interface BillOfMaterialService {

    List<BillOfMaterialResponse> getByProduct(Long productId);

    List<BillOfMaterialResponse> getByRawMaterial(Long rawMaterialId);

    BillOfMaterialResponse addBomItem(BillOfMaterialRequest request);

    BillOfMaterialResponse updateBomItem(
            Long id, BillOfMaterialRequest request);

    void deleteBomItem(Long id);
}