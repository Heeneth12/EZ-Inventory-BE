package com.ezh.Inventory.items.service;

import com.ezh.Inventory.items.dto.ItemDto;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public String createItem(ItemDto itemDto) throws CommonException {
        log.info("Creating new item: {}", itemDto);
        if (itemRepository.existsByItemCode(itemDto.getItemCode())) {
            throw new BadRequestException("Item Code already exists");
        }
        Item item = convertToEntity(itemDto);
        itemRepository.save(item);
        return "ITEM_CREATED_SUCCESSFULLY";
    }

    @Override
    @Transactional
    public String updateItem(Long id, ItemDto itemDto) {
        log.info("Updating item id: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Item not found"));

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setCategory(itemDto.getCategory());
        item.setSellingPrice(itemDto.getSellingPrice());
        item.setPurchasePrice(itemDto.getPurchasePrice());
        item.setTaxPercentage(itemDto.getTaxPercentage());
        item.setDiscountPercentage(itemDto.getDiscountPercentage());
        item.setUnitOfMeasure(itemDto.getUnitOfMeasure());
        item.setReorderLevel(itemDto.getReorderLevel());
        itemRepository.save(item);
        return "ITEM_UPDATED_SUCCESSFULLY";
    }


    @Override
    public ItemDto getItemById(Long id) {
        log.info("Fetching item by id: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Item not found"));

        return convertToDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        log.info("Fetching all items");

        return itemRepository.findAllByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String toggleItemActiveStatus(Long itemId) {
        log.info("Toggling active status for item {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException("ITEM_NOT_FOUND"));

        boolean updatedStatus = !item.getIsActive();
        item.setIsActive(updatedStatus);
        itemRepository.save(item);
        return updatedStatus ? "ITEM_ACTIVATED" : "ITEM_DEACTIVATED";
    }



    private Item convertToEntity(ItemDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .itemCode(dto.getItemCode())
                .sku(dto.getSku())
                .barcode(dto.getBarcode())
                .type(dto.getType())
                .category(dto.getCategory())
                .unitOfMeasure(dto.getUnitOfMeasure())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .mrp(dto.getMrp())
                .taxPercentage(dto.getTaxPercentage())
                .discountPercentage(dto.getDiscountPercentage())
                .openingStock(dto.getOpeningStock())
                .reorderLevel(dto.getReorderLevel())
                .warehouseId(dto.getWarehouseId())
                .hsnSacCode(dto.getHsnSacCode())
                .isActive(dto.getIsActive())
                .build();
    }


    private ItemDto convertToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .itemCode(item.getItemCode())
                .category(item.getCategory())
                .unitOfMeasure(item.getUnitOfMeasure())
                .purchasePrice(item.getPurchasePrice())
                .sellingPrice(item.getSellingPrice())
                .taxPercentage(item.getTaxPercentage())
                .discountPercentage(item.getDiscountPercentage())
                .openingStock(item.getOpeningStock())
                .reorderLevel(item.getReorderLevel())
                .isActive(item.getIsActive())
                .build();
    }
}
