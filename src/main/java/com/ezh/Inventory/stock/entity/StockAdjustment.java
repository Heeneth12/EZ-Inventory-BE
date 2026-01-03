package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment extends CommonSerializable {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "adjustment_number", unique = true)
    private String adjustmentNumber; // e.g., ADJ-2025-0001

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "adjustment_date")
    private Date adjustmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type")
    private AdjustmentType reasonType;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_status")
    private AdjustmentStatus adjustmentStatus;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "remarks")
    private String remarks;

    @Builder.Default
    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockAdjustmentItem> adjustmentItems = new ArrayList<>();
}
