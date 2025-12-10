package com.ezh.Inventory.sales.payment.entity;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends CommonSerializable {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "payment_number", nullable = false, unique = true, length = 40)
    private String paymentNumber; // PAY-2025-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Contact customer;

    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Total amount of this payment receipt

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Amount allocation tracking
    @Builder.Default
    @Column(name = "allocated_amount", nullable = false)
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "unallocated_amount", nullable = false)
    private BigDecimal unallocatedAmount = BigDecimal.ZERO;

    // Link to which invoices this payment is applied to
    @Builder.Default
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAllocation> allocations = new ArrayList<>();
}