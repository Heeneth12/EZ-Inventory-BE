package com.ezh.Inventory.stock.controller;

import com.ezh.Inventory.stock.dto.StockAdjustmentDto;
import com.ezh.Inventory.stock.dto.StockUpdateDto;
import com.ezh.Inventory.stock.service.StockAdjustmentService;
import com.ezh.Inventory.stock.service.StockService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockAdjustmentService stockAdjustmentService;

    /**
     * @Method : stockTransaction
     * @Discriptim :
     *
     * @param stockUpdateDto
     * @return CommonResponse
     * @throws CommonException
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> stockTransaction(@RequestBody StockUpdateDto stockUpdateDto) throws CommonException {
        log.info("Stock Transaction {}", stockUpdateDto);
        CommonResponse response = stockService.updateStock(stockUpdateDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "");
    }


    /**
     * @Method : createStockAdjustment
     * @Discriptim :
     *
     * @param stockAdjustmentDto
     * @return CommonResponse
     * @throws CommonException
     */
    @PostMapping(path = "/adjustment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createStockAdjustment(@RequestBody StockAdjustmentDto stockAdjustmentDto) throws CommonException {
        log.info("Entered Create Stock Adjustment with {}", stockAdjustmentDto);
        CommonResponse response = stockAdjustmentService.createStockAdjustment(stockAdjustmentDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "");
    }

}
