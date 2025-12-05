package com.ezh.Inventory.stock.dto;

import com.ezh.Inventory.stock.entity.AdjustmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentItemDto {
    @NotNull
    private Long itemId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private AdjustmentType adjustmentType;
}