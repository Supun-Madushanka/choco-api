package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.PayrollRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.PayrollResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Employee;
import com.ceylonechocolate.chocolate_factory_api.entity.Payroll;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.EmployeeRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.PayrollRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    // Sri Lanka statutory rates
    private static final BigDecimal EPF_EMPLOYEE_RATE =
            new BigDecimal("0.08");  // 8%
    private static final BigDecimal ETF_EMPLOYER_RATE =
            new BigDecimal("0.03");  // 3%

    @Override
    @Transactional
    public PayrollResponse createPayroll(PayrollRequest request,
                                         String processedByEmail) {

        if (payrollRepository.existsByEmployeeIdAndMonthAndYear(
                request.getEmployeeId(), request.getMonth(), request.getYear())) {
            throw new IllegalArgumentException(
                    "Payroll already exists for this employee for "
                            + request.getMonth() + "/" + request.getYear()
            );
        }

        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found")
                );

        User processedBy = userRepository
                .findByEmailAndIsDeletedFalse(processedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Payroll payroll = buildPayroll(
                employee, processedBy, request, Payroll.builder().build()
        );

        payrollRepository.save(payroll);
        log.info("Payroll created for employee {} - {}/{}",
                employee.getEmployeeNo(), request.getMonth(), request.getYear());

        return mapToResponse(payroll);
    }

    @Override
    @Transactional
    public PayrollResponse updatePayroll(Long id, PayrollRequest request,
                                         String processedByEmail) {

        Payroll payroll = findPayrollById(id);

        if (payroll.getPaymentStatus() == Payroll.PaymentStatus.PAID) {
            throw new IllegalArgumentException(
                    "Cannot update a payroll record that has already been paid"
            );
        }

        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found")
                );

        User processedBy = userRepository
                .findByEmailAndIsDeletedFalse(processedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        payroll = buildPayroll(employee, processedBy, request, payroll);
        payrollRepository.save(payroll);

        log.info("Payroll updated for employee {} - {}/{}",
                employee.getEmployeeNo(), request.getMonth(), request.getYear());

        return mapToResponse(payroll);
    }

    @Override
    public List<PayrollResponse> getPayrollByEmployee(Long employeeId) {
        return payrollRepository
                .findByEmployeeIdOrderByYearDescMonthDesc(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollResponse> getPayrollByMonthAndYear(
            Integer month, Integer year) {
        return payrollRepository.findByMonthAndYear(month, year)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PayrollResponse markAsPaid(Long id) {
        Payroll payroll = findPayrollById(id);

        if (payroll.getPaymentStatus() == Payroll.PaymentStatus.PAID) {
            throw new IllegalArgumentException(
                    "Payroll record is already marked as paid"
            );
        }

        payroll.setPaymentStatus(Payroll.PaymentStatus.PAID);
        payroll.setPaidAt(LocalDateTime.now());
        payrollRepository.save(payroll);

        log.info("Payroll marked as paid for employee {} - {}/{}",
                payroll.getEmployee().getEmployeeNo(),
                payroll.getMonth(), payroll.getYear());

        return mapToResponse(payroll);
    }

    @Override
    public PayrollResponse getMyPayrollLatest(String email) {
        Employee employee = getEmployeeByUserEmail(email);

        return payrollRepository
                .findByEmployeeIdOrderByYearDescMonthDesc(employee.getId())
                .stream()
                .findFirst()
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public List<PayrollResponse> getMyPayrollHistory(String email) {
        Employee employee = getEmployeeByUserEmail(email);

        return payrollRepository
                .findByEmployeeIdOrderByYearDescMonthDesc(employee.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // CALCULATION
    private Payroll buildPayroll(Employee employee, User processedBy,
                                 PayrollRequest request, Payroll payroll) {

        BigDecimal basicSalary = request.getBasicSalary();
        BigDecimal allowances = request.getAllowances() != null
                ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal additionalDeductions = request.getAdditionalDeductions() != null
                ? request.getAdditionalDeductions() : BigDecimal.ZERO;
        BigDecimal taxPercentage = request.getTaxPercentage() != null
                ? request.getTaxPercentage() : BigDecimal.ZERO;

        // Gross earnings = basic + allowances
        BigDecimal grossEarnings = basicSalary.add(allowances);

        // EPF Employee = 8% of basic salary
        BigDecimal epfEmployee = basicSalary
                .multiply(EPF_EMPLOYEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        // ETF Employer = 3% of basic salary (informational, employer pays)
        BigDecimal etfEmployer = basicSalary
                .multiply(ETF_EMPLOYER_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        // Tax = percentage of gross earnings
        BigDecimal tax = grossEarnings
                .multiply(taxPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Total deductions (employee side) = EPF + Tax + Additional
        BigDecimal totalDeductions = epfEmployee
                .add(tax)
                .add(additionalDeductions);

        // Net salary = gross earnings - total deductions
        BigDecimal netSalary = grossEarnings.subtract(totalDeductions);

        return payroll.toBuilder()
                .employee(employee)
                .month(request.getMonth())
                .year(request.getYear())
                .basicSalary(basicSalary)
                .allowances(allowances)
                .epfEmployee(epfEmployee)
                .etfEmployer(etfEmployer)
                .tax(tax)
                .additionalDeductions(additionalDeductions)
                .deductions(totalDeductions)
                .netSalary(netSalary)
                .processedBy(processedBy)
                .paymentStatus(
                        payroll.getPaymentStatus() != null
                                ? payroll.getPaymentStatus()
                                : Payroll.PaymentStatus.PENDING
                )
                .note(request.getNote())
                .build();
    }

    // HELPERS
    private Payroll findPayrollById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payroll record not found")
                );
    }

    private Employee getEmployeeByUserEmail(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No employee profile found for this account"
                        )
                );
    }

    private PayrollResponse mapToResponse(Payroll p) {
        return PayrollResponse.builder()
                .id(p.getId())
                .employeeId(p.getEmployee().getId())
                .employeeNo(p.getEmployee().getEmployeeNo())
                .employeeName(p.getEmployee().getFullName())
                .departmentName(p.getEmployee().getDepartment().getName())
                .month(p.getMonth())
                .year(p.getYear())
                .basicSalary(p.getBasicSalary())
                .allowances(p.getAllowances())
                .epfEmployee(p.getEpfEmployee())
                .etfEmployer(p.getEtfEmployer())
                .tax(p.getTax())
                .additionalDeductions(p.getAdditionalDeductions())
                .deductions(p.getDeductions())
                .netSalary(p.getNetSalary())
                .paymentStatus(p.getPaymentStatus().name())
                .paidAt(p.getPaidAt())
                .processedByName(p.getProcessedBy().getFullName())
                .note(p.getNote())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}