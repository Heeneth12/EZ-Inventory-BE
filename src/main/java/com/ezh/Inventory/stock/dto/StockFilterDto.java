package com.ezh.Inventory.stock.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockFilterDto {
    private Long id;
    private String searchQuery;
    private String status;
    private Long customerId;
    private Long itemId;
    private Long warehouseId;
    private Date fromDate;
    private Date toDate;
}
