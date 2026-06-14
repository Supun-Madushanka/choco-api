package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PayrollRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PayrollResponse;

import java.util.List;

public interface PayrollService {

    PayrollResponse createPayroll(PayrollRequest request, String processedByEmail);

    PayrollResponse updatePayroll(Long id, PayrollRequest request, String processedByEmail);

    List<PayrollResponse> getPayrollByEmployee(Long employeeId);

    List<PayrollResponse> getPayrollByMonthAndYear(Integer month, Integer year);

    PayrollResponse markAsPaid(Long id);

    PayrollResponse getMyPayrollLatest(String email);

    List<PayrollResponse> getMyPayrollHistory(String email);
}
