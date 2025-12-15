package com.ezh.Inventory.stock.dto;

import com.ezh.Inventory.stock.entity.AdjustmentType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentCreateDto {
    private Long warehouseId;
    private String reference;
    private String remarks;
    private AdjustmentType reasonType; // DAMAGE, EXPIRED
    private List<StockAdjustmentItemDto> items;
}
