package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.DepartmentRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.DepartmentResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Department;
import com.ceylonechocolate.chocolate_factory_api.repository.DepartmentRepository;
import com.ceylonechocolate.chocolate_factory_api.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found with id: " + id
                        )
                );
        return mapToResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        if (departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Department already exists with name: " + request.getName()
            );
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        departmentRepository.save(department);
        log.info("Department created: {}", department.getName());

        return mapToResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found with id: " + id
                        )
                );

        // Check name conflict with other departments
        if (!department.getName().equals(request.getName()) &&
                departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Department already exists with name: " + request.getName()
            );
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        departmentRepository.save(department);

        log.info("Department updated: {}", department.getName());
        return mapToResponse(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found with id: " + id
                        )
                );

        departmentRepository.delete(department);
        log.info("Department deleted: {}", department.getName());
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}