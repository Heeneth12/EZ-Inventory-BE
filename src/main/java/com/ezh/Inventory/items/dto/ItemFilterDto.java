package com.ezh.Inventory.items.dto;

import com.ezh.Inventory.items.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilterDto {
    String searchQuery;
    Boolean active;
    ItemType itemType;
    String brand;
    String category;
}
