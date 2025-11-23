package com.ezh.Inventory.items.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends CommonSerializable {

    // Basic Info
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "item_code", nullable = false, unique = true)
    private String itemCode;

    @Column(name = "sku")
    private String sku;

    @Column(name = "barcode")
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    // Classification
    @Column(name = "category")
    private String category;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    // Pricing
    @Column(name = "purchase_price", precision = 18, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 18, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "mrp", precision = 18, scale = 2)
    private BigDecimal mrp;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    // Inventory
    @Column(name = "opening_stock")
    private Integer openingStock;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "warehouse_id")
    private String warehouseId;

    // Tax Compliance
    @Column(name = "hsn_sac_code")
    private String hsnSacCode;

    // Status Flags
    @Column(name = "is_active")
    private Boolean isActive;

}
