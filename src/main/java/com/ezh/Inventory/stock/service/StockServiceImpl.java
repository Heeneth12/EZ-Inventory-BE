package com.ezh.Inventory.stock.service;

import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.stock.dto.StockAdjustmentDto;
import com.ezh.Inventory.stock.dto.StockUpdateDto;
import com.ezh.Inventory.stock.entity.Stock;
import com.ezh.Inventory.stock.entity.StockAdjustment;
import com.ezh.Inventory.stock.entity.StockLedger;
import com.ezh.Inventory.stock.repository.StockAdjustmentRepository;
import com.ezh.Inventory.stock.repository.StockLedgerRepository;
import com.ezh.Inventory.stock.repository.StockRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService, StockAdjustmentService {

    private final StockRepository stockRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public CommonResponse updateStock(StockUpdateDto dto) throws CommonException {
        Stock stock = stockRepository
                .findByItemIdAndWarehouseId(dto.getItemId(), dto.getWarehouseId())
                .orElse(createNewStock(dto.getItemId(), dto.getWarehouseId()));

        int beforeQty = stock.getClosingQty();
        int qty = dto.getQuantity();

        //Increase Stock transactionType IN or OUT
        if (dto.getTransactionType().equalsIgnoreCase("IN")) {
            stock.setInQty(stock.getInQty() + qty);
            stock.setClosingQty(beforeQty + qty);
        }
        //Reduce Stock
        else if (dto.getTransactionType().equalsIgnoreCase("OUT")) {
            if (beforeQty < qty) {
                throw new BadRequestException("Not enough stock available");
            }
            stock.setOutQty(stock.getOutQty() + qty);
            stock.setClosingQty(beforeQty - qty);
        }
        stockRepository.save(stock);

        //Ledger Entry — TRACK every stock movement
        StockLedger ledger = StockLedger.builder()
                .itemId(dto.getItemId())
                .warehouseId(dto.getWarehouseId())
                .transactionType(dto.getTransactionType())
                .quantity(qty)
                .referenceType(dto.getReferenceType()) // GRN, SALE, TRANSFER, ADJUSTMENT
                .referenceId(dto.getReferenceId())
                .beforeQty(beforeQty)
                .afterQty(stock.getClosingQty())
                .build();
        stockLedgerRepository.save(ledger);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(String.valueOf(ledger.getId()))
                .message("Stock updated successfully")
                .build();
    }


    @Override
    @Transactional
    public CommonResponse createStockAdjustment(StockAdjustmentDto stockAdjustmentDto) throws CommonException {

        Stock stock = stockRepository
                .findByItemIdAndWarehouseId(stockAdjustmentDto.getItemId(), stockAdjustmentDto.getWarehouseId())
                .orElseThrow(() -> new BadRequestException("Stock not found for item & warehouse"));

        int systemQty = stock.getClosingQty();
        int countedQty = stockAdjustmentDto.getCountedQty();
        int difference = countedQty - systemQty; // POSITIVE = IN | NEGATIVE = OUT

        //Save Stock Adjustment
        StockAdjustment adjustment = StockAdjustment.builder()
                .itemId(stockAdjustmentDto.getItemId())
                .warehouseId(stockAdjustmentDto.getWarehouseId())
                .reasonType(stockAdjustmentDto.getReasonType())
                .notes(stockAdjustmentDto.getNotes())
                .systemQty(systemQty)
                .countedQty(countedQty)
                .differenceQty(difference)
                .adjustedBy(stockAdjustmentDto.getAdjustedBy())
                .adjustedAt(System.currentTimeMillis())
                .build();
        stockAdjustmentRepository.save(adjustment);

        // If no difference – return success without stock movement
        if (difference == 0) {
            return CommonResponse.builder()
                    .status(Status.SUCCESS)
                    .id(String.valueOf(adjustment.getId()))
                    .message("Stock adjustment saved, no stock change needed")
                    .build();
        }

        //Prepare StockUpdateDto for universal stock update method
        StockUpdateDto updateDto = StockUpdateDto.builder()
                .itemId(stockAdjustmentDto.getItemId())
                .warehouseId(stockAdjustmentDto.getWarehouseId())
                .referenceType("ADJUSTMENT")
                .referenceId(adjustment.getId())    // link to adjustment table
                .build();

        if (difference > 0) {                   // +5 → EXTRA found
            updateDto.setQuantity(difference);
            updateDto.setTransactionType("IN"); // add to stock
        } else {                                // -3 → MISSING items
            updateDto.setQuantity(Math.abs(difference));
            updateDto.setTransactionType("OUT"); // reduce stock
        }
        updateStock(updateDto);


        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(String.valueOf(adjustment.getId()))
                .message("Stock adjusted successfully")
                .build();
    }


    private Stock createNewStock(Long itemId, Long warehouseId) {
        return Stock.builder()
                .itemId(itemId)
                .warehouseId(warehouseId)
                .openingQty(0)
                .inQty(0)
                .outQty(0)
                .closingQty(0)
                .build();
    }

}

//updateStock(new StockUpdateDto(itemId, warehouseId, receivedQty, "IN", "GRN", grnId, null));
//updateStock(new StockUpdateDto(itemId, warehouseId, soldQty, "OUT", "SALE", invoiceId, null));
//updateStock(new StockUpdateDto(itemId, warehouseId, 5, "IN", "ADJUSTMENT", auditId, "Extra found"));
//updateStock(new StockUpdateDto(itemId, warehouseId, 3, "OUT", "ADJUSTMENT", auditId, "Missing stock"));



