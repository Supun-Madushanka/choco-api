package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MaintenanceLogRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MaintenanceLogResponse;

import java.util.List;

public interface MaintenanceLogService {

    List<MaintenanceLogResponse> getAllLogs();

    List<MaintenanceLogResponse> getLogsByMachine(Long machineId);

    MaintenanceLogResponse createLog(
            MaintenanceLogRequest request, String performedByEmail);
}