package com.ezh.Inventory.sales.delivery.repository;

import com.ezh.Inventory.sales.delivery.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
    List<DeliveryItem> findByDeliveryId(Long deliveryId);

    @Query("SELECT SUM(di.quantity) FROM DeliveryItem di " +
            "WHERE di.delivery.invoice.id = :invoiceId AND di.itemId = :itemId " +
            "AND di.delivery.status = 'DELIVERED'")
    Integer getTotalDeliveredQtyForInvoiceItem(@Param("invoiceId") Long invoiceId,
                                               @Param("itemId") Long itemId);
}