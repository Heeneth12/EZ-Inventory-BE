package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment extends CommonSerializable {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false)
    private AdjustmentType reasonType;
    // Examples: DAMAGE, EXPIRED, AUDIT_CORRECTION, FOUND_EXTRA, LOST, SPILLAGE

    @Column(name = "notes")
    private String notes;

    @Column(name = "system_qty", nullable = false)
    private Integer systemQty; // Before adjustment

    @Column(name = "counted_qty", nullable = false)
    private Integer countedQty; // After physical counting

    @Column(name = "difference_qty", nullable = false)
    private Integer differenceQty; // counted_qty - system_qty (POSITIVE/NEGATIVE)

    @Column(name = "adjusted_by")
    private Long adjustedBy; // user id

    @Column(name = "adjusted_at")
    private Long adjustedAt; // timestamp
}
