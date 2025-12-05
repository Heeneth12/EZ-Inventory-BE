package com.ezh.Inventory.stock.service;

import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.stock.dto.*;
import com.ezh.Inventory.stock.entity.*;
import com.ezh.Inventory.stock.repository.StockAdjustmentRepository;
import com.ezh.Inventory.stock.repository.StockLedgerRepository;
import com.ezh.Inventory.stock.repository.StockRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.ezh.Inventory.utils.UserContextUtil.getTenantIdOrThrow;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public CommonResponse updateStock(StockUpdateDto dto) throws CommonException {

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Invalid quantity");
        }

        // 1. Fetch Stock with LOCK (Prevents Race Conditions)
        Stock stock = stockRepository
                .findByItemIdAndWarehouseIdAndTenantId(dto.getItemId(), dto.getWarehouseId(), getTenantIdOrThrow())
                .orElse(createNewStock(dto.getItemId(), dto.getWarehouseId()));

        int qty = dto.getQuantity();
        int beforeQty = stock.getClosingQty();
        BigDecimal transactionPrice = dto.getUnitPrice() != null ? dto.getUnitPrice() : BigDecimal.ZERO;

        // 2. Handle IN (Purchase, Returns, Adjustment Up)
        if (dto.getTransactionType() == MovementType.IN) {

            // CALCULATE WEIGHTED AVERAGE COST (Only for purchases/positive value adds)
            if (transactionPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal currentTotalValue = stock.getAverageCost().multiply(BigDecimal.valueOf(beforeQty));
                BigDecimal incomingTotalValue = transactionPrice.multiply(BigDecimal.valueOf(qty));

                BigDecimal newTotalValue = currentTotalValue.add(incomingTotalValue);
                BigDecimal newTotalQty = BigDecimal.valueOf(beforeQty + qty);

                // Avoid division by zero
                if (newTotalQty.compareTo(BigDecimal.ZERO) > 0) {
                    // Rounding Mode HALF_UP is standard for currency
                    stock.setAverageCost(newTotalValue.divide(newTotalQty, 2, RoundingMode.HALF_UP));
                }
            }

            stock.setInQty(stock.getInQty() + qty);
            stock.setClosingQty(beforeQty + qty);

            // Update total Stock Value based on new Average
            stock.setStockValue(stock.getAverageCost().multiply(BigDecimal.valueOf(stock.getClosingQty())));
        }

        // 3. Handle OUT (Sales, Write-off, Adjustment Down)
        if (dto.getTransactionType() == MovementType.OUT) {
            if (beforeQty < qty) {
                throw new BadRequestException("Not enough stock available. Current: " + beforeQty);
            }
            stock.setOutQty(stock.getOutQty() + qty);
            stock.setClosingQty(beforeQty - qty);

            // Average Cost DOES NOT CHANGE on OUT, but Total Value decreases
            stock.setStockValue(stock.getAverageCost().multiply(BigDecimal.valueOf(stock.getClosingQty())));
        }

        stockRepository.save(stock);

        // 4. Ledger Entry
        StockLedger ledger = StockLedger.builder()
                .itemId(dto.getItemId())
                .tenantId(getTenantIdOrThrow())
                .warehouseId(dto.getWarehouseId())
                .transactionType(dto.getTransactionType())
                .referenceType(dto.getReferenceType())
                .referenceId(dto.getReferenceId())
                .quantity(qty)
                .beforeQty(beforeQty)
                .afterQty(stock.getClosingQty())
                .unitPrice(transactionPrice) // Save the price of this specific transaction
                .build();

        stockLedgerRepository.save(ledger);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(String.valueOf(ledger.getId()))
                .message("Stock updated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockDto> getCurrentStock(StockFilterDto filterDto, Integer page, Integer size) throws CommonException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stocks = stockRepository.findAll(pageable);
        return stocks.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockLedgerDto> getStockTransactions(StockFilterDto filterDto, Integer page, Integer size) throws CommonException {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockLedger> stockLedger = stockLedgerRepository.findAll(pageable);
        return stockLedger.map(this::convertToDTO);
    }


    @Override
    @Transactional
    public CommonResponse createStockAdjustment(StockAdjustmentBatchDto batchDto) throws CommonException {

        Long tenantId = getTenantIdOrThrow();
        Long warehouseId = batchDto.getWarehouseId();
        AdjustmentMode batchMode = batchDto.getMode(); // <--- Read Mode Once

        for (StockAdjustmentItemDto itemDto : batchDto.getItems()) {

            // 1. Get Current Stock
            Stock stock = stockRepository
                    .findByItemIdAndWarehouseIdAndTenantId(itemDto.getItemId(), warehouseId, tenantId)
                    .orElse(createNewStock(itemDto.getItemId(), warehouseId));

            int systemQty = stock.getClosingQty();
            int finalCountedQty;
            int difference;

            // 2. Apply the BATCH MODE to this item
            switch (batchMode) {
                case REMOVE:
                    // Batch is for Damages/Losses
                    // Input 5 means: "Remove 5". Result: 100 - 5 = 95
                    difference = -itemDto.getQuantity();
                    finalCountedQty = systemQty - itemDto.getQuantity();
                    break;

                case ADD:
                    // Batch is for Found items/Recoveries
                    // Input 5 means: "Add 5". Result: 100 + 5 = 105
                    difference = itemDto.getQuantity();
                    finalCountedQty = systemQty + itemDto.getQuantity();
                    break;

                case ABSOLUTE:
                default:
                    // Batch is for Stock Take/Audit
                    // Input 95 means: "Count is 95". Result: 95 - 100 = -5
                    finalCountedQty = itemDto.getQuantity();
                    difference = finalCountedQty - systemQty;
                    break;
            }

            // Safety Check
            if (finalCountedQty < 0) {
                throw new BadRequestException("Adjustment would result in negative stock for Item ID: " + itemDto.getItemId());
            }

            if (difference == 0) continue;

            // 3. Save Adjustment Record
            StockAdjustment adjustment = StockAdjustment.builder()
                    .itemId(itemDto.getItemId())
                    .tenantId(tenantId)
                    .warehouseId(warehouseId)
                    .reasonType(itemDto.getAdjustmentType())
                    .notes(batchDto.getNotes())
                    .systemQty(systemQty)
                    .countedQty(finalCountedQty)
                    .differenceQty(difference)
                    .adjustedBy(1L) // Replace with User Context
                    .adjustedAt(System.currentTimeMillis())
                    .build();

            stockAdjustmentRepository.save(adjustment);

            // 4. Update Stock & Ledger
            MovementType movementType = (difference > 0) ? MovementType.IN : MovementType.OUT;

            StockUpdateDto updateDto = StockUpdateDto.builder()
                    .itemId(itemDto.getItemId())
                    .warehouseId(warehouseId)
                    .quantity(Math.abs(difference))
                    .transactionType(movementType)
                    .referenceType(ReferenceType.ADJUSTMENT)
                    .referenceId(adjustment.getId())
                    .unitPrice(stock.getAverageCost())
                    .build();

            updateStock(updateDto);
        }

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .message("Stock adjustment batch processed successfully")
                .build();
    }


    private Stock createNewStock(Long itemId, Long warehouseId) {
        return Stock.builder()
                .itemId(itemId)
                .tenantId(getTenantIdOrThrow())
                .warehouseId(warehouseId)
                .openingQty(0)
                .inQty(0)
                .outQty(0)
                .closingQty(0)
                .build();
    }

    private StockDto convertToDTO(Stock stock) {
        return StockDto.builder()
                .id(stock.getId())
                .itemId(stock.getItemId())
                .warehouseId(stock.getWarehouseId())
                .openingQty(stock.getOpeningQty())
                .inQty(stock.getInQty())
                .outQty(stock.getOutQty())
                .closingQty(stock.getClosingQty())
                .build();
    }

    private StockLedgerDto convertToDTO(StockLedger stockLedger) {
        return StockLedgerDto.builder()
                .id(stockLedger.getId())
                .itemId(stockLedger.getItemId())
                .warehouseId(stockLedger.getWarehouseId())
                .transactionType(stockLedger.getTransactionType())
                .quantity(stockLedger.getQuantity())
                .referenceType(stockLedger.getReferenceType())
                .referenceId(stockLedger.getReferenceId())
                .beforeQty(stockLedger.getBeforeQty())
                .afterQty(stockLedger.getAfterQty())
                .build();
    }
}

//updateStock(new StockUpdateDto(itemId, warehouseId, receivedQty, "IN", "GRN", grnId, null));
//updateStock(new StockUpdateDto(itemId, warehouseId, soldQty, "OUT", "SALE", invoiceId, null));
//updateStock(new StockUpdateDto(itemId, warehouseId, 5, "IN", "ADJUSTMENT", auditId, "Extra found"));
//updateStock(new StockUpdateDto(itemId, warehouseId, 3, "OUT", "ADJUSTMENT", auditId, "Missing stock"));



