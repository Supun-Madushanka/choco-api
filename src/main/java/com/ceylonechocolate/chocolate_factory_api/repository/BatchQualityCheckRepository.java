package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.BatchQualityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchQualityCheckRepository
        extends JpaRepository<BatchQualityCheck, Long> {

    List<BatchQualityCheck> findByProductionBatchIdOrderByCheckedAtDesc(
            Long productionBatchId);
}