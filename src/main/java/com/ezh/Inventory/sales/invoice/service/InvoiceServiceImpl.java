package com.ezh.Inventory.sales.invoice.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.sales.delivery.service.DeliveryService;
import com.ezh.Inventory.sales.invoice.dto.InvoiceCreateDto;
import com.ezh.Inventory.sales.invoice.dto.InvoiceDto;
import com.ezh.Inventory.sales.invoice.dto.InvoiceItemCreateDto;
import com.ezh.Inventory.sales.invoice.dto.InvoiceItemDto;
import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.sales.invoice.entity.InvoiceItem;
import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import com.ezh.Inventory.sales.invoice.repository.InvoiceItemRepository;
import com.ezh.Inventory.sales.invoice.repository.InvoiceRepository;
import com.ezh.Inventory.sales.order.entity.SalesOrder;
import com.ezh.Inventory.sales.order.entity.SalesOrderItem;
import com.ezh.Inventory.sales.order.entity.SalesOrderStatus;
import com.ezh.Inventory.sales.order.repository.SalesOrderItemRepository;
import com.ezh.Inventory.sales.order.repository.SalesOrderRepository;
import com.ezh.Inventory.stock.dto.StockUpdateDto;
import com.ezh.Inventory.stock.entity.MovementType;
import com.ezh.Inventory.stock.entity.ReferenceType;
import com.ezh.Inventory.stock.service.StockService;
import com.ezh.Inventory.utils.UserContextUtil;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final ItemRepository itemRepository;
    private final ContactRepository contactRepository;
    private final StockService stockService;
    private final DeliveryService deliveryService;


    @Override
    @Transactional
    public CommonResponse createInvoice(InvoiceCreateDto dto) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        // A. Validate Sales Order
        SalesOrder salesOrder = salesOrderRepository.findByIdAndTenantId(dto.getSalesOrderId(), tenantId)
                .orElseThrow(() -> new CommonException("Sales order not found", HttpStatus.NOT_FOUND));

        if (salesOrder.getStatus() == SalesOrderStatus.FULLY_INVOICED || salesOrder.getStatus() == SalesOrderStatus.CANCELLED) {
            throw new BadRequestException("Sales Order is already completed or cancelled");
        }

        // B. Validate Contact
        Contact contact = contactRepository.findByIdAndTenantId(dto.getCustomerId(), tenantId)
                .orElseThrow(() -> new CommonException("Customer not found", HttpStatus.NOT_FOUND));

        // C. Create Invoice Header
        Invoice invoice = Invoice.builder()
                .tenantId(tenantId)
                .warehouseId(salesOrder.getWarehouseId()) // Inherit warehouse
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(new Date())
                .salesOrder(salesOrder)
                .customer(contact)
                .status(InvoiceStatus.PENDING)
                .discountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO)
                .amountPaid(BigDecimal.ZERO)
                .balance(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .subTotal(BigDecimal.ZERO)
                .remarks(dto.getRemarks())
                .build();

        invoiceRepository.save(invoice); // Save to generate ID

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO; // Add logic if you track tax per item
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        // D. Process Line Items
        for (InvoiceItemCreateDto itemDto : dto.getItems()) {

            // 1. Validate against Order Line
            SalesOrderItem soItem = salesOrderItemRepository.findById(itemDto.getSoItemId())
                    .orElseThrow(() -> new BadRequestException("Invalid SO Line Item ID"));

            if (!soItem.getSalesOrder().getId().equals(salesOrder.getId())) {
                throw new BadRequestException("Item does not belong to this Sales Order");
            }

            // 2. Validate Quantity (Prevent over-invoicing)
            int remainingQty = soItem.getOrderedQty() - soItem.getInvoicedQty();
            if (itemDto.getQuantity() > remainingQty) {
                throw new BadRequestException("Cannot invoice " + itemDto.getQuantity() + " for " + soItem.getItemName() + ". Only " + remainingQty + " remaining.");
            }

            // 3. Fetch Snapshot Data
            Item itemMaster = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new CommonException("Item Master not found", HttpStatus.NOT_FOUND));

            // 4. Calculate Financials
            BigDecimal price = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : soItem.getUnitPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subTotal = subTotal.add(lineTotal);

            // 5. Create Invoice Item (Snapshot)
            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .soItemId(soItem.getId())
                    .itemId(itemMaster.getId())
                    .itemName(itemMaster.getName()) // Snapshot Name
                    .sku(itemMaster.getSku())       // Snapshot SKU
                    .batchNumber(itemDto.getBatchNumber()) // Specific Batch
                    .quantity(itemDto.getQuantity())
                    .unitPrice(price)
                    .lineTotal(lineTotal)
                    .build();

            invoiceItems.add(invoiceItem);

            // 6. UPDATE STOCK (Critical: Deducts from Inventory)
            StockUpdateDto stockUpdate = StockUpdateDto.builder()
                    .itemId(itemDto.getItemId())
                    .warehouseId(salesOrder.getWarehouseId())
                    .quantity(itemDto.getQuantity())
                    .transactionType(MovementType.OUT)
                    .referenceType(ReferenceType.SALE)
                    .referenceId(invoice.getId())
                    .batchNumber(itemDto.getBatchNumber()) // Critical for specific costing
                    .build();

            stockService.updateStock(stockUpdate);

            // 7. Update Sales Order Progress
            soItem.setInvoicedQty(soItem.getInvoicedQty() + itemDto.getQuantity());
            salesOrderItemRepository.save(soItem);
        }

        invoiceItemRepository.saveAll(invoiceItems);

        // E. Finalize Invoice Header
        invoice.setSubTotal(subTotal);
        BigDecimal grandTotal = subTotal.subtract(invoice.getDiscountAmount());
        invoice.setGrandTotal(grandTotal);
        invoice.setBalance(grandTotal); // Balance is full amount initially
        invoiceRepository.save(invoice);

        //TRIGGER DELIVERY LOGIC
        deliveryService.createDeliveryForInvoice(invoice, dto);

        // F. Update Sales Order Status
        updateSalesOrderStatus(salesOrder);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(invoice.getId().toString())
                .message("Invoice Created Successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        Invoice invoice = invoiceRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CommonException("Invoice not found", HttpStatus.NOT_FOUND));

        return mapToDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoices(Integer page, Integer size) {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices = invoiceRepository.findByTenantId(tenantId, pageable);
        return invoices.map(this::mapToDto);
    }


    private void updateSalesOrderStatus(SalesOrder salesOrder) {
        List<SalesOrderItem> allItems = salesOrderItemRepository.findBySalesOrderId(salesOrder.getId());

        boolean allFullyInvoiced = true;
        boolean anyInvoiced = false;

        for (SalesOrderItem item : allItems) {
            if (item.getInvoicedQty() > 0) anyInvoiced = true;
            if (item.getInvoicedQty() < item.getOrderedQty()) {
                allFullyInvoiced = false;
            }
        }

        if (allFullyInvoiced) {
            salesOrder.setStatus(SalesOrderStatus.FULLY_INVOICED);
        } else if (anyInvoiced) {
            salesOrder.setStatus(SalesOrderStatus.PARTIALLY_INVOICED);
        }

        salesOrderRepository.save(salesOrder);
    }

    public InvoiceDto mapToDto(Invoice invoice) {
        List<InvoiceItemDto> itemDtos = invoice.getItems().stream()
                .map(item -> InvoiceItemDto.builder()
                        .id(item.getId())
                        .itemId(item.getItemId())
                        .itemName(item.getItemName())
                        .sku(item.getSku())
                        .batchNumber(item.getBatchNumber())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .salesOrderId(invoice.getSalesOrder() != null ? invoice.getSalesOrder().getId() : null)
                .customerName(invoice.getCustomer().getName())
                .invoiceDate(invoice.getInvoiceDate())
                .status(invoice.getStatus())
                .subTotal(invoice.getSubTotal())
                .grandTotal(invoice.getGrandTotal())
                .balance(invoice.getBalance())
                .items(itemDtos)
                .build();
    }

    private String generateInvoiceNumber() {
        int random = new Random().nextInt(9000) + 1000;
        return "INV-" + System.currentTimeMillis() + "-" + random;
    }
}