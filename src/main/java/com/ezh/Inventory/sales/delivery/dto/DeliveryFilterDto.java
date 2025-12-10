package com.ezh.Inventory.sales.delivery.dto;

import com.ezh.Inventory.sales.delivery.entity.ShipmentStatus;
import com.ezh.Inventory.sales.delivery.entity.ShipmentType;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryFilterDto {
    private Long id;
    private String deliveryNumber;
    private Long invoiceId;
    private Long customerId;
    private ShipmentType type;   // PICKUP / COURIER / OWN_FLEET
    private ShipmentStatus status; // PENDING, SCHEDULED, SHIPPED, DELIVERED
    private Date scheduledDate;
    private Date shippedDate;
    private Date deliveredDate;
}
