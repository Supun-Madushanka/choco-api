package com.ceylonechocolate.chocolate_factory_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private Long departmentId;
    private String departmentName;
    private String employeeNo;
    private String fullName;
    private String phone;
    private String nic;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate joinedDate;
    private String employmentType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}