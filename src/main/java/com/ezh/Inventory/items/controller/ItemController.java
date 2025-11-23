package com.ezh.Inventory.items.controller;

import com.ezh.Inventory.items.dto.ItemDto;
import com.ezh.Inventory.items.service.ItemService;
import com.ezh.Inventory.utils.common.ResponseResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<String> createItem(@RequestBody ItemDto itemDto) {
        log.info("Creating new item with SKU: {}", itemDto);
        String response = itemService.createItem(itemDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "ITEM CREATED SUCCESSFULLY");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<List<ItemDto>> getAllItems() {
        log.info("Fetching all items");
        List<ItemDto> response = itemService.getAllItems();
        return ResponseResource.success(HttpStatus.OK, response, "ITEM LIST FETCHED");
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<ItemDto> getItemById(@PathVariable Long id) {
        log.info("Fetching item with ID: {}", id);
        ItemDto response = itemService.getItemById(id);
        return ResponseResource.success(HttpStatus.OK, response, "ITEM DETAILS FETCHED");
    }


    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<String> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        log.info("Updating item with ID: {}", id);
        String response = itemService.updateItem(id, itemDto);
        return ResponseResource.success(HttpStatus.OK, response, "ITEM UPDATED SUCCESSFULLY");
    }

    @PostMapping(value = "/{id}/toggle-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<?> toggleItemActiveStatus(@PathVariable Long id) {
        log.info("Soft deleting item with ID: {}", id);
        String response = itemService.toggleItemActiveStatus(id);
        return ResponseResource.success(HttpStatus.OK, response, "ITEM TOGGLED SUCCESSFULLY");
    }


//    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> searchItems(@RequestParam("query") String query) {
//        log.info("Searching items by query: {}", query);
//        List<ItemResponse> results = itemService.search(query);
//        return ResponseResource.success(HttpStatus.OK, results, "SEARCH RESULTS");
//    }
}
