package com.ezh.Inventory.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentDto {
    @NotNull(message = "Item ID is required")
    private Long itemId;
    private Long warehouseId;
    @NotBlank(message = "Reason type is required")
    private String reasonType;
    private String notes;
    private String transactionType;    // IN or OUT
    private String referenceType; //  it bydefaut stock adjustemt
    private Integer countedQty;
    private Long adjustedBy;
    private Long adjustedAt;
}
