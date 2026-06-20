package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BatchQualityCheckRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BatchQualityCheckResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.BatchQualityCheck;
import com.ceylonechocolate.chocolate_factory_api.entity.ProductionBatch;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.BatchQualityCheckRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductionBatchRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.BatchQualityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchQualityCheckServiceImpl
        implements BatchQualityCheckService {

    private final BatchQualityCheckRepository qualityCheckRepository;
    private final ProductionBatchRepository productionBatchRepository;
    private final UserRepository userRepository;

    @Override
    public List<BatchQualityCheckResponse> getChecksByBatch(Long batchId) {
        return qualityCheckRepository
                .findByProductionBatchIdOrderByCheckedAtDesc(batchId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BatchQualityCheckResponse addCheck(
            Long batchId, BatchQualityCheckRequest request,
            String checkedByEmail) {

        ProductionBatch batch = productionBatchRepository
                .findById(batchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Production batch not found"
                        )
                );

        User checkedBy = userRepository
                .findByEmailAndIsDeletedFalse(checkedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        BatchQualityCheck check = BatchQualityCheck.builder()
                .productionBatch(batch)
                .checkType(BatchQualityCheck.CheckType
                        .valueOf(request.getCheckType()))
                .result(BatchQualityCheck.Result
                        .valueOf(request.getResult()))
                .checkedBy(checkedBy)
                .notes(request.getNotes())
                .build();

        qualityCheckRepository.save(check);

        log.info("Quality check added for batch {}: {} - {}",
                batch.getBatchNumber(), request.getCheckType(),
                request.getResult());

        return mapToResponse(check);
    }

    private BatchQualityCheckResponse mapToResponse(
            BatchQualityCheck check) {
        return BatchQualityCheckResponse.builder()
                .id(check.getId())
                .productionBatchId(check.getProductionBatch().getId())
                .batchNumber(check.getProductionBatch().getBatchNumber())
                .checkType(check.getCheckType().name())
                .result(check.getResult().name())
                .checkedByName(check.getCheckedBy().getFullName())
                .notes(check.getNotes())
                .checkedAt(check.getCheckedAt())
                .build();
    }
}