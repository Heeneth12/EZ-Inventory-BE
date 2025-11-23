package com.ezh.Inventory.stock.service;


import com.ezh.Inventory.stock.dto.StockUpdateDto;
import com.ezh.Inventory.utils.common.CommonResponse;

public interface StockService {

    CommonResponse updateStock(StockUpdateDto stockUpdateDto);

}
