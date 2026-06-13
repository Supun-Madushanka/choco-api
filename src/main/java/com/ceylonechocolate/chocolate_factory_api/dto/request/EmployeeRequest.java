package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

    private Long userId;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotBlank(message = "Employee number is required")
    private String employeeNo;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String nic;

    private String address;

    private LocalDate dateOfBirth;

    private String gender;

    @NotNull(message = "Joined date is required")
    private LocalDate joinedDate;

    @NotBlank(message = "Employment type is required")
    private String employmentType;
}