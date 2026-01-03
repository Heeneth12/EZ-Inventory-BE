package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_adjustment_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentItem extends CommonSerializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjustment_id", nullable = false)
    private StockAdjustment stockAdjustment;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "system_qty")
    private Integer systemQty; // Expected qty in DB at that moment

    @Column(name = "counted_qty")
    private Integer countedQty; // Actual physical qty

    @Column(name = "difference_qty")
    private Integer differenceQty; // Calculated: counted - system

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", length = 50)
    private AdjustmentType reasonType;
}
