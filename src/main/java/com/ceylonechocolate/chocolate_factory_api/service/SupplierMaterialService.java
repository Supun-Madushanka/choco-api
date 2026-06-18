package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierMaterialRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierMaterialResponse;

import java.util.List;

public interface SupplierMaterialService {

    List<SupplierMaterialResponse> getMaterialsBySupplier(Long supplierId);

    List<SupplierMaterialResponse> getSuppliersByMaterial(Long rawMaterialId);

    List<SupplierMaterialResponse> getPreferredSuppliersByMaterial(
            Long rawMaterialId);

    SupplierMaterialResponse addSupplierMaterial(
            SupplierMaterialRequest request);

    SupplierMaterialResponse updateSupplierMaterial(
            Long id, SupplierMaterialRequest request);

    void deleteSupplierMaterial(Long id);
}