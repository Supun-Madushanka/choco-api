package com.ceylonechocolate.chocolate_factory_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "production_batches")
public class ProductionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", nullable = false, unique = true, length = 30)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_produced", precision = 12, scale = 2)
    private BigDecimal quantityProduced;

    @Column(name = "quantity_rejected", precision = 12, scale = 2)
    private BigDecimal quantityRejected = BigDecimal.ZERO;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "qc_status")
    private QcStatus qcStatus = QcStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "qc_marked_by")
    private User qcMarkedBy;

    @Column(name = "qc_marked_at")
    private LocalDateTime qcMarkedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status")
    private FinalStatus finalStatus = FinalStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "final_approved_by")
    private User finalApprovedBy;

    @Column(name = "final_approved_at")
    private LocalDateTime finalApprovedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status = BatchStatus.IN_PROGRESS;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supervised_by", nullable = false)
    private User supervisedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

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

    public enum QcStatus {
        PENDING, PASSED, FAILED
    }

    public enum FinalStatus {
        PENDING, APPROVED, REJECTED, REPROCESS
    }

    public enum BatchStatus {
        IN_PROGRESS, QC_PENDING, QC_DONE,
        STOCKED, REJECTED, REPROCESS
    }
}