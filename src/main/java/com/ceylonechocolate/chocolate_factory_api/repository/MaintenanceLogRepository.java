package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceLogRepository
        extends JpaRepository<MaintenanceLog, Long> {

    List<MaintenanceLog> findByMachineIdOrderByMaintenanceDateDesc(
            Long machineId);

    List<MaintenanceLog> findAllByOrderByMaintenanceDateDesc();
}