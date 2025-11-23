package com.ezh.Inventory.items.dto;

import com.ezh.Inventory.items.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private String itemCode;
    private String sku;
    private String barcode;
    private ItemType type;
    private String category;
    private String unitOfMeasure;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private BigDecimal taxPercentage;
    private BigDecimal discountPercentage;
    private Integer openingStock;
    private Integer reorderLevel;
    private String warehouseId;
    private String hsnSacCode;
    private Boolean isActive;
}
