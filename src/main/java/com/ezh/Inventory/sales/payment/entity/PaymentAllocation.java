package com.ezh.Inventory.sales.payment.entity;

import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "payment_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAllocation extends CommonSerializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "allocated_amount", nullable = false)
    private BigDecimal allocatedAmount; // How much of this payment goes to this invoice

    @Column(name = "allocation_date", nullable = false)
    private Date allocationDate;

    // PAYMENT METHOD DETAILS - Can vary per allocation!
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // Check number, transaction ID, UPI ref, etc.

    @Column(name = "bank_name", length = 100)
    private String bankName; // For bank transfers, cheques

    @Column(name = "transaction_id", length = 100)
    private String transactionId; // For online payments

    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;

    @Column(name = "cheque_date")
    private Date chequeDate;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour; // Last 4 digits of card

    @Column(name = "upi_id", length = 100)
    private String upiId; // UPI ID for UPI payments

    @Column(name = "remarks", length = 500)
    private String remarks;
}
