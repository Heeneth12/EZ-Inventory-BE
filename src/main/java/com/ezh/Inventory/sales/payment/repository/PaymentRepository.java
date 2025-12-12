package com.ezh.Inventory.sales.payment.repository;

import com.ezh.Inventory.sales.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByTenantId(Long tenantId, Pageable pageable);
}
