package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MaintenanceLogRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MaintenanceLogResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Machine;
import com.ceylonechocolate.chocolate_factory_api.entity.MaintenanceLog;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.MachineRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.MaintenanceLogRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.MaintenanceLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceLogServiceImpl implements MaintenanceLogService {

    private final MaintenanceLogRepository maintenanceLogRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;

    @Override
    public List<MaintenanceLogResponse> getAllLogs() {
        return maintenanceLogRepository.findAllByOrderByMaintenanceDateDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceLogResponse> getLogsByMachine(Long machineId) {
        return maintenanceLogRepository
                .findByMachineIdOrderByMaintenanceDateDesc(machineId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaintenanceLogResponse createLog(
            MaintenanceLogRequest request, String performedByEmail) {

        Machine machine = machineRepository
                .findByIdAndIsDeletedFalse(request.getMachineId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Machine not found")
                );

        User performedBy = userRepository
                .findByEmailAndIsDeletedFalse(performedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        MaintenanceLog maintenanceLog = MaintenanceLog.builder()
                .machine(machine)
                .maintenanceType(MaintenanceLog.MaintenanceType
                        .valueOf(request.getMaintenanceType()))
                .description(request.getDescription())
                .maintenanceDate(request.getMaintenanceDate())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .cost(request.getCost() != null
                        ? request.getCost() : java.math.BigDecimal.ZERO)
                .performedBy(performedBy)
                .build();

        maintenanceLogRepository.save(maintenanceLog);

        // Auto-update machine's maintenance dates
        machine.setLastMaintenance(request.getMaintenanceDate());
        machine.setNextMaintenance(request.getNextMaintenanceDate());

        // If this was a breakdown fix, set machine back to operational
        if (machine.getStatus() == Machine.MachineStatus.BREAKDOWN ||
                machine.getStatus() == Machine.MachineStatus.MAINTENANCE) {
            machine.setStatus(Machine.MachineStatus.OPERATIONAL);
        }

        machineRepository.save(machine);

        log.info("Maintenance logged for machine {}: {}",
                machine.getCode(), request.getMaintenanceType());

        return mapToResponse(maintenanceLog);
    }

    private MaintenanceLogResponse mapToResponse(MaintenanceLog log) {
        return MaintenanceLogResponse.builder()
                .id(log.getId())
                .machineId(log.getMachine().getId())
                .machineCode(log.getMachine().getCode())
                .machineName(log.getMachine().getName())
                .maintenanceType(log.getMaintenanceType().name())
                .description(log.getDescription())
                .maintenanceDate(log.getMaintenanceDate())
                .nextMaintenanceDate(log.getNextMaintenanceDate())
                .cost(log.getCost())
                .performedByName(log.getPerformedBy().getFullName())
                .createdAt(log.getCreatedAt())
                .build();
    }
}