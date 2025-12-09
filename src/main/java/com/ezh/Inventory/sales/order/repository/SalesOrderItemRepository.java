package com.ezh.Inventory.sales.order.repository;


import com.ezh.Inventory.sales.order.entity.SalesOrder;
import com.ezh.Inventory.sales.order.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem> findBySalesOrderId(Long id);
}
