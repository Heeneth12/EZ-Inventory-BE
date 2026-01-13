package com.ezh.Inventory.purchase.grn.dto;

import com.ezh.Inventory.purchase.grn.entity.GrnStatus;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrnDto {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private String grnNumber;
    private Long purchaseOrderId;
    private String supplierInvoiceNo; // Optional
    private GrnStatus status;
    private Date createdAt;
    private List<GrnItemDto> items;
}
