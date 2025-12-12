package com.ezh.Inventory.sales.delivery.controller;


import com.ezh.Inventory.sales.delivery.dto.DeliveryDto;
import com.ezh.Inventory.sales.delivery.dto.DeliveryFilterDto;
import com.ezh.Inventory.sales.delivery.service.DeliveryService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping(value = "/{deliveryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<DeliveryDto> getDeliveryDetail(@PathVariable Long deliveryId) throws CommonException {
        log.info("Fetching delivery details for id: {}", deliveryId);
        DeliveryDto response = deliveryService.getDeliveryDetail(deliveryId);
        return ResponseResource.success(HttpStatus.OK, response, "Delivery details fetched successfully");
    }

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<DeliveryDto>> getDeliveryDetails(@RequestParam Integer page, @RequestParam Integer size) throws CommonException {
        log.info("Fetching all deliveries with page: {} and size: {}", page, size);
        Page<DeliveryDto> response = deliveryService.getAllDeliveries(page, size);
        return ResponseResource.success(HttpStatus.OK, response, "Deliveries fetched successfully");
    }

    @PostMapping(value = "/{deliveryId}/delivered", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> markAsDelivered(@PathVariable Long deliveryId) throws CommonException {
        log.info("Marking delivery {} as delivered", deliveryId);
        CommonResponse response = deliveryService.markAsDelivered(deliveryId);
        return ResponseResource.success(HttpStatus.OK, response, "Delivery marked as delivered successfully");
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<List<DeliveryDto>> searchDeliveryDetails(@RequestBody DeliveryFilterDto filter) throws CommonException {
        log.info("Searching deliveries with filter: {}", filter);
        List<DeliveryDto> response = deliveryService.searchDeliveryDetails(filter);
        return ResponseResource.success(HttpStatus.OK, response, "Deliveries fetched successfully based on search criteria");
    }

}
