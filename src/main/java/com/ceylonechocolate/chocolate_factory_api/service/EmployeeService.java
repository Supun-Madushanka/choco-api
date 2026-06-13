package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.EmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.UpdateEmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> getEmployeesByDepartment(Long departmentId);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse getEmployeeByUserId(Long userId);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);

    void deactivateEmployee(Long id);

    void activateEmployee(Long id);
}