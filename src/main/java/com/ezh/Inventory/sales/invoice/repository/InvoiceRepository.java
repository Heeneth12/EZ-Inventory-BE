package com.ezh.Inventory.sales.invoice.repository;

import com.ezh.Inventory.sales.invoice.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

     Boolean existsByInvoiceNumber(String invoiceNumber);

     Optional<Invoice> findByIdAndTenantId(Long id, Long tenantId);
     Page<Invoice> findByTenantId(Long tenantId, Pageable pageable);
}
