package com.ezh.Inventory.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDto {
    private Long itemId;
    private Long warehouseId;
    private Integer quantity;
    private String transactionType;    // IN or OUT
    private String referenceType;      // Who triggered stock change? (GRN, SALE, TRANSFER, ADJUSTMENT, sold for custoe etc.)
    private Long referenceId;          // ID of the document that triggered stock change
    private String remarks;
}
