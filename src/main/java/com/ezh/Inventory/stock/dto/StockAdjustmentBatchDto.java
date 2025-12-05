package com.ezh.Inventory.stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentBatchDto {
    private Long warehouseId;
    @NotNull(message = "Mode is required")
    private AdjustmentMode mode;
    private String notes;
    private List<StockAdjustmentItemDto> items;
}