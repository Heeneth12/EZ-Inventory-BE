package com.ezh.Inventory.items.service;

import com.ezh.Inventory.items.dto.ItemDto;

import java.util.List;

public interface ItemService {

    String createItem(ItemDto itemDto);
    String updateItem(Long id, ItemDto itemDto);
    ItemDto getItemById(Long id);
    List<ItemDto> getAllItems();
    String toggleItemActiveStatus(Long id);

}
