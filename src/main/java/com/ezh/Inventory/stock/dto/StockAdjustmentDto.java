package com.ezh.Inventory.stock.dto;

import com.ezh.Inventory.stock.entity.AdjustmentStatus;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentDto {
    private Long id;
    private String adjustmentNumber;
    private Date adjustmentDate;
    private AdjustmentStatus status;
    private Long warehouseId;
    private String reference;
    private int totalItems;
}
