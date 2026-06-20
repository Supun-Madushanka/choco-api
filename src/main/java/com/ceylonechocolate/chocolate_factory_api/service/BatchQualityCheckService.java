package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BatchQualityCheckRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BatchQualityCheckResponse;

import java.util.List;

public interface BatchQualityCheckService {

    List<BatchQualityCheckResponse> getChecksByBatch(Long batchId);

    BatchQualityCheckResponse addCheck(
            Long batchId, BatchQualityCheckRequest request,
            String checkedByEmail);
}