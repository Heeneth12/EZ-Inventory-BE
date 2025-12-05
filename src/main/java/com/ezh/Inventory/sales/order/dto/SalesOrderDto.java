package com.ezh.Inventory.sales.order.dto;

import com.ezh.Inventory.sales.order.entity.SalesOrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderDto {

    private Long id;              // only for update
    private Long tenantId;
    private String orderNumber;   // SO-001, SO-2025-001
    private LocalDate orderDate;
    private Long customerId;      // Contact ID (Customer)
    private String customerName;
    private String paymentTerms;// "Net 30", "Advance", etc.
    private BigDecimal subTotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalDiscountPer;
    private SalesOrderStatus status;
    private BigDecimal grandTotal;
    private List<SalesOrderItemDto> items; // CHILD ITEMS
    private String remarks;
}
