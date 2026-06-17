package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.RawMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.RawMaterialResponse;

import java.util.List;

public interface RawMaterialService {

    List<RawMaterialResponse> getAllRawMaterials();

    List<RawMaterialResponse> getActiveRawMaterials();

    List<RawMaterialResponse> getRawMaterialsByCategory(Long categoryId);

    RawMaterialResponse getRawMaterialById(Long id);

    RawMaterialResponse createRawMaterial(RawMaterialRequest request);

    RawMaterialResponse updateRawMaterial(Long id, RawMaterialRequest request);

    void deleteRawMaterial(Long id);
}