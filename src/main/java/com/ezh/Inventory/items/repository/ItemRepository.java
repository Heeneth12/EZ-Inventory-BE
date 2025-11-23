package com.ezh.Inventory.items.repository;

import com.ezh.Inventory.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemCode(String itemCode);

    List<Item> findAllByIsActiveTrue();


}
