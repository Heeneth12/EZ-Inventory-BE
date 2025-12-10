package com.ezh.Inventory.sales.delivery.service;

import com.ezh.Inventory.sales.delivery.dto.DeliveryDto;
import com.ezh.Inventory.sales.delivery.dto.DeliveryFilterDto;
import com.ezh.Inventory.sales.delivery.entity.ShipmentStatus;
import com.ezh.Inventory.sales.invoice.dto.InvoiceCreateDto;
import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeliveryService {

    CommonResponse createDeliveryFromInvoice(Long invoiceId) throws CommonException;

    CommonResponse updateDeliveryStatus(Long deliveryId, ShipmentStatus newStatus) throws CommonException;

    Page<DeliveryDto> getAllDeliveries(int page, int size) throws CommonException;

    DeliveryDto getDeliveryDetail(Long deliveryId) throws CommonException;

    List<DeliveryDto> searchDeliveryDetails(DeliveryFilterDto filter) throws CommonException;

    void createDeliveryForInvoice(Invoice invoice, InvoiceCreateDto dto);

    CommonResponse markAsShipped(Long deliveryId, String trackingNumber);

    CommonResponse markAsDelivered(Long deliveryId);
}
