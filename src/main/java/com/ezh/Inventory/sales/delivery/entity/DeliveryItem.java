package com.ezh.Inventory.sales.delivery.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryItem extends CommonSerializable {

    private Long itemId;

    private String itemName;

    @Column(name = "invoice_item_id")
    private Long invoiceItemId;

    @Column(name = "batch_number")
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(nullable = false)
    private Integer quantity;
}
