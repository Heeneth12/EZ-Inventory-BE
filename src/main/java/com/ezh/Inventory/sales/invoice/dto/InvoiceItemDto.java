package com.ezh.Inventory.sales.invoice.dto;

import com.ezh.Inventory.sales.invoice.entity.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {
    private Long id;
    private Invoice invoice;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private  String batchNumber;
    private String sku;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount; // optional per item
    private BigDecimal taxAmount; // tax per item
    private BigDecimal lineTotal; // qty × price − discount + tax
}
