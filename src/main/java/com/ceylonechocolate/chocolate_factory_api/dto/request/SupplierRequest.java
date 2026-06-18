package com.ceylonechocolate.chocolate_factory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Supplier type is required")
    private String supplierType;

    private String status;
}