package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.SupplierRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    List<SupplierResponse> getAllSuppliers();

    List<SupplierResponse> getActiveSuppliers();

    SupplierResponse getSupplierById(Long id);

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(Long id);

    String getNextSupplierCode();
}