package com.ezh.Inventory.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentItemDto {
    private Long itemId;
    private Integer quantity;
    private String batchNumber; // Optional
}