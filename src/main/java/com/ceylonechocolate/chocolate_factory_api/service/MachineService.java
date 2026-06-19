package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MachineRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MachineResponse;

import java.util.List;

public interface MachineService {

    List<MachineResponse> getAllMachines();

    List<MachineResponse> getMachinesByStatus(String status);

    MachineResponse getMachineById(Long id);

    MachineResponse createMachine(MachineRequest request);

    MachineResponse updateMachine(Long id, MachineRequest request);

    void deleteMachine(Long id);

    String getNextMachineCode();
}