package com.ezh.Inventory.sales.invoice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCreateDto {
    private Long salesOrderId;
    private Long customerId;
    private String customerName;
    private List<InvoiceItemCreateDto> items;
    private BigDecimal discountAmount;
    private String remarks;
}
