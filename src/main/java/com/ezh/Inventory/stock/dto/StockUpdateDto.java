package com.ezh.Inventory.stock.dto;

import com.ezh.Inventory.stock.entity.MovementType;
import com.ezh.Inventory.stock.entity.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDto {
    private Long itemId;
    private Long warehouseId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private MovementType transactionType;    // IN or OUT
    private ReferenceType referenceType;      // Who triggered stock change? (GRN, SALE, TRANSFER, ADJUSTMENT, sold etc.)
    private Long referenceId;          // ID of the document that triggered stock change
    private String remarks;
}
