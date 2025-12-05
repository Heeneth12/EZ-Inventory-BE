package com.ezh.Inventory.sales.order.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.sales.order.dto.SalesOrderCreateDto;
import com.ezh.Inventory.sales.order.dto.SalesOrderDto;
import com.ezh.Inventory.sales.order.dto.SalesOrderFilter;
import com.ezh.Inventory.sales.order.dto.SalesOrderItemDto;
import com.ezh.Inventory.sales.order.entity.SalesOrder;
import com.ezh.Inventory.sales.order.entity.SalesOrderItem;
import com.ezh.Inventory.sales.order.entity.SalesOrderStatus;
import com.ezh.Inventory.sales.order.repository.SalesOrderItemRepository;
import com.ezh.Inventory.sales.order.repository.SalesOrderRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ezh.Inventory.utils.UserContextUtil.getTenantIdOrThrow;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ItemRepository itemRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final ContactRepository contactRepository;


    @Override
    @Transactional
    public CommonResponse createSalesOrder(SalesOrderCreateDto salesOrderCreateDto) throws CommonException {
        log.info("Creating new Sales Order: {}", salesOrderCreateDto);

        SalesOrder salesOrder = new SalesOrder();
        mapDtoToEntity(salesOrderCreateDto, salesOrder, SalesOrderStatus.PENDING);

        salesOrderRepository.save(salesOrder);

        return CommonResponse.builder()
                .id(salesOrder.getId().toString())
                .message("SALES_ORDER_CREATED_SUCCESSFULLY")
                .status(Status.SUCCESS)
                .build();
    }

    /**
     * Update Sales Order
     */
    @Override
    @Transactional
    public CommonResponse updateSalesOrder(Long id, SalesOrderCreateDto salesOrderCreateDto) throws CommonException {
        log.info("Updating Sales Order id: {}", id);

        SalesOrder salesOrder = salesOrderRepository.findByIdAndTenantId(id, getTenantIdOrThrow())
                .orElseThrow(() -> new BadRequestException("Sales Order not found"));

        mapDtoToEntity(salesOrderCreateDto, salesOrder, null);
        salesOrderRepository.save(salesOrder);

        return CommonResponse.builder()
                .id(salesOrder.getId().toString())
                .message("SALES_ORDER_UPDATED_SUCCESSFULLY")
                .status(Status.SUCCESS)
                .build();
    }

    @Override
    public Page<SalesOrderDto> getAllSalesOrders(SalesOrderFilter filter, int page, int size) throws CommonException {
        log.info("Fetching all sales orders with filter : {}", filter);
        Pageable pageable = PageRequest.of(page, size);
        Page<SalesOrder> salesOrders = salesOrderRepository.findAll(pageable);
        return salesOrders.map(this::convertToDto);
    }

    @Override
    public SalesOrderDto getSalesOrder(Long id) throws CommonException {
        log.info("Fetching Sales Order id: {}", id);

        SalesOrder salesOrder = salesOrderRepository.findByIdAndTenantId(id, getTenantIdOrThrow())
                .orElseThrow(() -> new BadRequestException("Sales Order not found"));

        return convertToDto(salesOrder);
    }


    /**
     * PRIVATE MAPPER — Convert SalesOrderCreateDto → SalesOrder Entity
     */
    private void mapDtoToEntity(SalesOrderCreateDto dto, SalesOrder salesOrder, SalesOrderStatus status) {

        salesOrder.setOrderNumber(dto.getOrderNumber());
        salesOrder.setOrderDate(dto.getOrderDate());
        salesOrder.setRemarks(dto.getRemarks());
        salesOrder.setTenantId(getTenantIdOrThrow());

        if (status != null) {
            salesOrder.setStatus(status);
        }

        // Set Customer
        Contact customer = contactRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new BadRequestException("Invalid Customer ID: " + dto.getCustomerId()));
        salesOrder.setCustomer(customer);

        // Clear existing items if editing
        if (salesOrder.getId() != null && salesOrder.getItems() != null && !salesOrder.getItems().isEmpty()) {
            salesOrderItemRepository.deleteAll(salesOrder.getItems());
            salesOrder.getItems().clear();
        } else {
            salesOrder.setItems(new ArrayList<>());
        }

        List<SalesOrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (SalesOrderItemDto itemDto : dto.getItems()) {

            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new BadRequestException("Invalid Item ID: " + itemDto.getItemId()));

            SalesOrderItem orderItem = new SalesOrderItem();
            orderItem.setSalesOrder(salesOrder);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());

            BigDecimal qty = BigDecimal.valueOf(itemDto.getQuantity());
            BigDecimal unitPrice = item.getMrp();
            BigDecimal discount = itemDto.getDiscount() != null ? itemDto.getDiscount() : BigDecimal.ZERO;
            BigDecimal computedLineTotal = qty.multiply(unitPrice).subtract(discount);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setDiscount(discount);
            orderItem.setLineTotal(computedLineTotal);

            // Accumulate totals
            subTotal = subTotal.add(qty.multiply(unitPrice));
            totalDiscount = totalDiscount.add(discount);

            items.add(orderItem);
        }

        salesOrder.getItems().addAll(items);
        // Calculate grand total
        BigDecimal grandTotal = subTotal.subtract(totalDiscount).add(totalTax);

        salesOrder.setSubTotal(subTotal);
        salesOrder.setTotalDiscount(totalDiscount);
        salesOrder.setGrandTotal(grandTotal);
    }


    private SalesOrderDto convertToDto(SalesOrder entity) {

        List<SalesOrderItemDto> itemDtos = new ArrayList<>();

        for (SalesOrderItem item : entity.getItems()) {
            SalesOrderItemDto itemDto = SalesOrderItemDto.builder()
                    .itemId(item.getItem().getId())
                    .itemName(item.getItem().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .discount(item.getDiscount())
                    .lineTotal(item.getLineTotal())
                    .build();

            itemDtos.add(itemDto);
        }

        // Compute grand total safely with BigDecimal
        BigDecimal grandTotal = entity.getItems().stream()
                .map(SalesOrderItem::getLineTotal)          // Stream<BigDecimal>
                .filter(Objects::nonNull)                   // avoid nulls
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // sum all

        BigDecimal discount = entity.getTotalDiscount();
        BigDecimal discountPercent = BigDecimal.ZERO;

        discountPercent = discount
                .divide(grandTotal, 4, RoundingMode.HALF_UP) // discount / subtotal
                .multiply(BigDecimal.valueOf(100));


        return SalesOrderDto.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .orderNumber(entity.getOrderNumber())
                .orderDate(entity.getOrderDate())
                .remarks(entity.getRemarks())
                .status(entity.getStatus())
                .customerId(entity.getCustomer().getId())
                .customerName(entity.getCustomer().getName())
                .totalDiscount(entity.getTotalDiscount())
                .totalDiscountPer(discountPercent) //(totalDiscount / totalAmount) * 100
                .items(itemDtos)
                .grandTotal(grandTotal)
                .build();
    }
}
