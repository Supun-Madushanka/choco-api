package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.EmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.UpdateEmployeeRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.EmployeeResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Department;
import com.ceylonechocolate.chocolate_factory_api.entity.Employee;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.DepartmentRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.EmployeeRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository
                .findByDepartmentIdAndIsDeletedFalse(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeById(id);
        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found for user id: " + userId
                        )
                );
        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        // Check employee number
        if (employeeRepository.existsByEmployeeNoAndIsDeletedFalse(
                request.getEmployeeNo())) {
            throw new IllegalArgumentException(
                    "Employee number already exists: " + request.getEmployeeNo()
            );
        }

        // Check NIC
        if (request.getNic() != null && !request.getNic().isBlank() &&
                employeeRepository.existsByNicAndIsDeletedFalse(request.getNic())) {
            throw new IllegalArgumentException(
                    "NIC already exists: " + request.getNic()
            );
        }

        // Load department
        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Department not found")
                );

        // Load user if provided
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found")
                    );
        }

        Employee employee = Employee.builder()
                .user(user)
                .department(department)
                .employeeNo(request.getEmployeeNo())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .nic(request.getNic())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender() != null
                        ? Employee.Gender.valueOf(request.getGender())
                        : null)
                .joinedDate(request.getJoinedDate())
                .employmentType(Employee.EmploymentType
                        .valueOf(request.getEmploymentType()))
                .status(Employee.EmployeeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        employeeRepository.save(employee);
        log.info("Employee created: {}", employee.getEmployeeNo());

        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {

        Employee employee = findEmployeeById(id);

        // Check employee number conflict
        if (!employee.getEmployeeNo().equals(request.getEmployeeNo()) &&
                employeeRepository.existsByEmployeeNoAndIsDeletedFalse(
                        request.getEmployeeNo())) {
            throw new IllegalArgumentException(
                    "Employee number already exists: " + request.getEmployeeNo()
            );
        }

        // Check NIC conflict
        if (request.getNic() != null && !request.getNic().isBlank() &&
                !request.getNic().equals(employee.getNic()) &&
                employeeRepository.existsByNicAndIsDeletedFalse(request.getNic())) {
            throw new IllegalArgumentException(
                    "NIC already exists: " + request.getNic()
            );
        }

        // Load department if changed
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Department not found")
                    );
            employee.setDepartment(department);
        }

        if (request.getEmployeeNo() != null && !request.getEmployeeNo().isBlank()) {
            employee.setEmployeeNo(request.getEmployeeNo());
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            employee.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            employee.setPhone(request.getPhone());
        }

        if (request.getNic() != null && !request.getNic().isBlank()) {
            employee.setNic(request.getNic());
        }

        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            employee.setAddress(request.getAddress());
        }

        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getGender() != null) {
            employee.setGender(Employee.Gender.valueOf(request.getGender()));
        }

        if (request.getEmploymentType() != null) {
            employee.setEmploymentType(Employee.EmploymentType
                    .valueOf(request.getEmploymentType()));
        }

        employeeRepository.save(employee);
        log.info("Employee updated: {}", employee.getEmployeeNo());

        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = findEmployeeById(id);

        if (employee.getStatus() == Employee.EmployeeStatus.INACTIVE) {
            throw new IllegalArgumentException("Employee is already inactive");
        }

        employee.setStatus(Employee.EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        log.info("Employee deactivated: {}", employee.getEmployeeNo());
    }

    @Override
    @Transactional
    public void activateEmployee(Long id) {
        Employee employee = findEmployeeById(id);

        if (employee.getStatus() == Employee.EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("Employee is already active");
        }

        employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        employeeRepository.save(employee);
        log.info("Employee activated: {}", employee.getEmployeeNo());
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id
                        )
                );
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUser() != null
                        ? employee.getUser().getId() : null)
                .userEmail(employee.getUser() != null
                        ? employee.getUser().getEmail() : null)
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getName())
                .employeeNo(employee.getEmployeeNo())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .nic(employee.getNic())
                .address(employee.getAddress())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender() != null
                        ? employee.getGender().name() : null)
                .joinedDate(employee.getJoinedDate())
                .employmentType(employee.getEmploymentType().name())
                .status(employee.getStatus().name())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}