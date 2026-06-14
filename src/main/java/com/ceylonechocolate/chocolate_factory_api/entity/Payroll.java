package com.ceylonechocolate.chocolate_factory_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payroll")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "epf_employee", precision = 12, scale = 2)
    private BigDecimal epfEmployee = BigDecimal.ZERO;

    @Column(name = "etf_employer", precision = 12, scale = 2)
    private BigDecimal etfEmployer = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "additional_deductions", precision = 12, scale = 2)
    private BigDecimal additionalDeductions = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PaymentStatus {
        PENDING, PAID
    }
}