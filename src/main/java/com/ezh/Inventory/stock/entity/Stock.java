package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock extends CommonSerializable {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "opening_qty", nullable = false)
    private Integer openingQty = 0;

    @Column(name = "in_qty", nullable = false)
    private Integer inQty = 0;

    @Column(name = "out_qty", nullable = false)
    private Integer outQty = 0;

    @Column(name = "closing_qty", nullable = false)
    private Integer closingQty = 0;

    @Column(name = "average_cost", precision = 18, scale = 2)
    private BigDecimal averageCost = BigDecimal.ZERO;

    @Column(name = "stock_value", precision = 18, scale = 2)
    private BigDecimal stockValue = BigDecimal.ZERO;
}
