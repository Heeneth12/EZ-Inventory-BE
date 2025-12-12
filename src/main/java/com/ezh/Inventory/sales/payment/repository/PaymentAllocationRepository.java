package com.ezh.Inventory.sales.payment.repository;

import com.ezh.Inventory.sales.payment.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, Long> {

    List<PaymentAllocation> findByInvoiceIdAndTenantIdOrderByAllocationDateDesc(Long invoiceId, Long tenantId);
}
