package com.ezh.Inventory.stock.repository;

import com.ezh.Inventory.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);
}
