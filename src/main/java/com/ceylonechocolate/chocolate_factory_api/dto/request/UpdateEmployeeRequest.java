package com.ceylonechocolate.chocolate_factory_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeRequest {

    private String employeeNo;
    private Long departmentId;
    private String fullName;
    private String phone;
    private String nic;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private String employmentType;
}