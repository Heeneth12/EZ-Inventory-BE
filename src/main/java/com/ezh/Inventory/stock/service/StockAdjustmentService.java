package com.ezh.Inventory.stock.service;

import com.ezh.Inventory.stock.dto.StockAdjustmentDto;
import com.ezh.Inventory.utils.common.CommonResponse;

public interface StockAdjustmentService {

    CommonResponse createStockAdjustment(StockAdjustmentDto stockAdjustmentDto);
}

