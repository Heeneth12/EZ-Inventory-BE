package com.ezh.Inventory.sales.payment.dto;

import com.ezh.Inventory.sales.payment.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto {
    private Long invoiceId;
    private BigDecimal amount;
    private PaymentMethod paymentMode;
    private String referenceNumber;
    private Date paymentDate;
    private String remarks;
}