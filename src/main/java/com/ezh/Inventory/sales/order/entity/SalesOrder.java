package com.ezh.Inventory.sales.order.entity;


import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sales_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder extends CommonSerializable {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;  // SO-2025-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Contact customer; // Existing Contact table used as Customer

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private SalesOrderStatus status;  // CREATED | PARTIALLY_DELIVERED | DELIVERED | CANCELLED

    @Column(name = "sub_total")
    private BigDecimal subTotal; // Sum of (qty * unitPrice) for all items

    @Column(name = "total_discount")
    private BigDecimal totalDiscount; // Sum of all item discounts

    @Column(name = "grand_total", nullable = false)
    private BigDecimal grandTotal; // subTotal - totalDiscount + totalTax

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
