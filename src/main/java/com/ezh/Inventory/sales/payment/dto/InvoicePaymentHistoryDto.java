package com.ezh.Inventory.sales.payment.dto;

import com.ezh.Inventory.sales.payment.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePaymentHistoryDto {
    private Long id;
    private Long paymentId;
    private String paymentNumber;
    private Date paymentDate;
    private BigDecimal amount; // The amount applied to THIS invoice
    private PaymentMethod method;
    private String referenceNumber; // Cheque No / Transaction ID
    private String remarks;
}