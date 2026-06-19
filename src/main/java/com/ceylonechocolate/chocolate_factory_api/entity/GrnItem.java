package com.ceylonechocolate.chocolate_factory_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grn_items")
public class GrnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceivedNote grn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "ordered_quantity", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal orderedQuantity;

    @Column(name = "received_quantity", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal receivedQuantity;

    @Column(name = "accepted_quantity", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal acceptedQuantity;

    @Column(name = "rejected_quantity", precision = 12, scale = 2)
    private BigDecimal rejectedQuantity = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_status")
    private QualityStatus qualityStatus = QualityStatus.PENDING;

    @Column(name = "quality_notes", columnDefinition = "TEXT")
    private String qualityNotes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inspected_by")
    private User inspectedBy;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    public enum QualityStatus {
        PENDING, PASSED, FAILED
    }
}