package com.ezh.Inventory.items.repository;

import com.ezh.Inventory.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemCode(String itemCode);

    List<Item> findAllByIsActiveTrue();

    @Query("""
                SELECT i FROM Item i
                WHERE i.isActive = true AND (
                       LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                       LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :query, '%')) OR
                       LOWER(i.barcode) LIKE LOWER(CONCAT('%', :query, '%'))
                )
                ORDER BY 
                    CASE 
                        WHEN LOWER(i.name) = LOWER(:query) THEN 1
                        WHEN LOWER(i.itemCode) = LOWER(:query) THEN 2
                        ELSE 3
                    END,
                    i.name ASC
            """)
    List<Item> smartSearch(@Param("query") String query);
}
