package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_ledger")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLedger extends CommonSerializable {

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private MovementType transactionType;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private ReferenceType referenceType;

    @Column(name = "reference_id")  //GRN_ID / SALE_ID ...
    private Long referenceId;

    @Column(name = "before_qty")
    private Integer beforeQty;

    @Column(name = "after_qty")
    private Integer afterQty;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice; // Price per item (Purchase price or Selling price)

    @Column(name = "total_value", precision = 18, scale = 2)
    private BigDecimal totalValue; // quantity * unit_price
}
