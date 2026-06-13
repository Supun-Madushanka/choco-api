package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.DepartmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse createDepartment(DepartmentRequest departmentRequest);

    DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest);

    void deleteDepartment(Long id);
}
