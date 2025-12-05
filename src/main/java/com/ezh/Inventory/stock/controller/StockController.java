package com.ezh.Inventory.stock.controller;

import com.ezh.Inventory.stock.dto.*;
import com.ezh.Inventory.stock.service.StockService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/stock")
@CrossOrigin(value = "*")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> stockUpdate(@RequestBody StockUpdateDto stockUpdateDto) throws CommonException {
        log.info("Entered Stock Update with : {}", stockUpdateDto);
        CommonResponse response = stockService.updateStock(stockUpdateDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "Stock updated successfully");
    }

    @PostMapping(value = "/all", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<StockDto>> getCurrentStock(@RequestParam Integer page, @RequestParam Integer size,
                                                            @RequestBody StockFilterDto filter) throws CommonException {
        log.info("Entered get current stock with : {}", filter);
        Page<StockDto> response = stockService.getCurrentStock(filter, page, size);
        return ResponseResource.success(HttpStatus.OK, response, "Stock fetched successfully");
    }

    @PostMapping(value = "/ledger", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<StockLedgerDto>> getStockTransactions(@RequestParam Integer page, @RequestParam Integer size,
                                                                       @RequestBody StockFilterDto filter) throws CommonException {
        log.info("Entered get stockTransactions with {}", filter);
        Page<StockLedgerDto> response = stockService.getStockTransactions(filter, page, size);
        return ResponseResource.success(HttpStatus.OK, response, "fetched all stock ledger");
    }

    @PostMapping(path = "/adjustment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createStockAdjustment(@RequestBody StockAdjustmentBatchDto  stockAdjustmentBatchDto) throws CommonException {
        log.info("Entered Create Stock Adjustment with {}", stockAdjustmentBatchDto);
        CommonResponse response = stockService.createStockAdjustment(stockAdjustmentBatchDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "Stock adjustment successfully");
    }

}
