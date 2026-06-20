package com.ceylonechocolate.chocolate_factory_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "batch_quality_checks")
public class BatchQualityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_batch_id", nullable = false)
    private ProductionBatch productionBatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false)
    private CheckType checkType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checked_by", nullable = false)
    private User checkedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "checked_at", updatable = false)
    private LocalDateTime checkedAt;

    @PrePersist
    protected void onCreate() {
        checkedAt = LocalDateTime.now();
    }

    public enum CheckType {
        VISUAL, TASTE, WEIGHT, PACKAGING, LAB_TEST
    }

    public enum Result {
        PASS, FAIL
    }
}