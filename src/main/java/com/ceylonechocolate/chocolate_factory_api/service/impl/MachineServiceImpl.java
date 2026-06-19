package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MachineRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MachineResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Machine;
import com.ceylonechocolate.chocolate_factory_api.repository.MachineRepository;
import com.ceylonechocolate.chocolate_factory_api.service.MachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;

    @Override
    public List<MachineResponse> getAllMachines() {
        return machineRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MachineResponse> getMachinesByStatus(String status) {
        return machineRepository
                .findByStatusAndIsDeletedFalse(
                        Machine.MachineStatus.valueOf(status))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MachineResponse getMachineById(Long id) {
        return mapToResponse(findMachineById(id));
    }

    @Override
    @Transactional
    public MachineResponse createMachine(MachineRequest request) {

        if (request.getSerialNo() != null &&
                !request.getSerialNo().isBlank() &&
                machineRepository.existsBySerialNo(request.getSerialNo())) {
            throw new IllegalArgumentException(
                    "Machine already exists with serial no: " +
                            request.getSerialNo()
            );
        }

        String code = getNextMachineCode();

        Machine machine = Machine.builder()
                .code(code)
                .name(request.getName())
                .model(request.getModel())
                .serialNo(request.getSerialNo())
                .purchaseDate(request.getPurchaseDate())
                .lastMaintenance(request.getLastMaintenance())
                .nextMaintenance(request.getNextMaintenance())
                .status(request.getStatus() != null
                        ? Machine.MachineStatus.valueOf(request.getStatus())
                        : Machine.MachineStatus.OPERATIONAL)
                .isDeleted(false)
                .build();

        machineRepository.save(machine);
        log.info("Machine created: {}", machine.getCode());

        return mapToResponse(machine);
    }

    @Override
    @Transactional
    public MachineResponse updateMachine(Long id, MachineRequest request) {

        Machine machine = findMachineById(id);

        if (request.getSerialNo() != null &&
                !request.getSerialNo().isBlank() &&
                !request.getSerialNo().equals(machine.getSerialNo()) &&
                machineRepository.existsBySerialNo(request.getSerialNo())) {
            throw new IllegalArgumentException(
                    "Machine already exists with serial no: " +
                            request.getSerialNo()
            );
        }

        machine.setName(request.getName());
        machine.setModel(request.getModel());
        machine.setSerialNo(request.getSerialNo());
        machine.setPurchaseDate(request.getPurchaseDate());
        machine.setLastMaintenance(request.getLastMaintenance());
        machine.setNextMaintenance(request.getNextMaintenance());
        if (request.getStatus() != null) {
            machine.setStatus(Machine.MachineStatus
                    .valueOf(request.getStatus()));
        }

        machineRepository.save(machine);
        log.info("Machine updated: {}", machine.getCode());

        return mapToResponse(machine);
    }

    @Override
    @Transactional
    public void deleteMachine(Long id) {
        Machine machine = findMachineById(id);
        machine.setIsDeleted(true);
        machine.setDeletedAt(LocalDateTime.now());
        machineRepository.save(machine);
        log.info("Machine deleted: {}", machine.getCode());
    }

    @Override
    public String getNextMachineCode() {
        return machineRepository.findLastCode()
                .map(last -> {
                    int lastNumber = Integer.parseInt(
                            last.replace("MCH-", "")
                    );
                    return String.format("MCH-%03d", lastNumber + 1);
                })
                .orElse("MCH-001");
    }

    private Machine findMachineById(Long id) {
        return machineRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Machine not found with id: " + id
                        )
                );
    }

    private MachineResponse mapToResponse(Machine machine) {
        return MachineResponse.builder()
                .id(machine.getId())
                .code(machine.getCode())
                .name(machine.getName())
                .model(machine.getModel())
                .serialNo(machine.getSerialNo())
                .purchaseDate(machine.getPurchaseDate())
                .lastMaintenance(machine.getLastMaintenance())
                .nextMaintenance(machine.getNextMaintenance())
                .status(machine.getStatus().name())
                .createdAt(machine.getCreatedAt())
                .updatedAt(machine.getUpdatedAt())
                .build();
    }
}