package com.ezh.Inventory.sales.payment.entity;

public enum PaymentStatus {
    DRAFT,          // Payment created but not confirmed
    PENDING,        // Waiting for clearance (e.g., cheque)
    CLEARED,        // Payment cleared/confirmed
    ALLOCATED,      // Fully allocated to invoices
    PARTIALLY_ALLOCATED, // Some amount still unallocated
    CANCELLED,      // Payment cancelled
    REFUNDED
}
