package com.ezh.Inventory.sales.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrderFilter {
    private Long id;
    private String status;
    private Long customerId;
    private Long warehouseId;
}