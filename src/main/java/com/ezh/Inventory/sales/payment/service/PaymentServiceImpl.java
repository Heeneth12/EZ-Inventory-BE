package com.ezh.Inventory.sales.payment.service;

import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import com.ezh.Inventory.sales.invoice.repository.InvoiceRepository;
import com.ezh.Inventory.sales.payment.dto.PaymentCreateDto;
import com.ezh.Inventory.sales.payment.entity.*;
import com.ezh.Inventory.sales.payment.repository.PaymentAllocationRepository;
import com.ezh.Inventory.sales.payment.repository.PaymentRepository;
import com.ezh.Inventory.utils.UserContextUtil;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public CommonResponse recordPayment(PaymentCreateDto dto) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        // 1. Fetch Invoice
        Invoice invoice = invoiceRepository.findByIdAndTenantId(dto.getInvoiceId(), tenantId)
                .orElseThrow(() -> new CommonException("Invoice not found", HttpStatus.NOT_FOUND));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        // 2. Validate Amount (Cannot pay more than balance)
        if (dto.getAmount().compareTo(invoice.getBalance()) > 0) {
            throw new BadRequestException("Payment amount (" + dto.getAmount() + ") exceeds remaining balance (" + invoice.getBalance() + ")");
        }

        // 3. Create Payment Header (The Receipt)
        Payment payment = Payment.builder()
                .tenantId(tenantId)
                .paymentNumber("PAY-" + System.currentTimeMillis())
                .customer(invoice.getCustomer())
                .paymentDate(new Date())
                .amount(dto.getAmount())
                .status(PaymentStatus.PENDING)
                .allocatedAmount(dto.getAmount())
                .unallocatedAmount(BigDecimal.ZERO)
                .remarks(dto.getRemarks())
                .build();

        paymentRepository.save(payment);

        // 4. Create Allocation (Linking Receipt to Invoice)
        PaymentAllocation allocation = PaymentAllocation.builder()
                .payment(payment)
                .invoice(invoice)
                .allocatedAmount(dto.getAmount())
                .allocationDate(new Date())
                .paymentMethod(dto.getPaymentMode())
                .referenceNumber(dto.getReferenceNumber())
                .build();

        allocationRepository.save(allocation);

        // 5. Update Invoice Status & Balance
        BigDecimal newPaid = invoice.getAmountPaid().add(dto.getAmount());
        BigDecimal newBalance = invoice.getGrandTotal().subtract(newPaid);

        invoice.setAmountPaid(newPaid);
        invoice.setBalance(newBalance);

        // Check if fully paid (Balance == 0)
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(payment.getId().toString())
                .message("Payment of " + dto.getAmount() + " recorded successfully")
                .build();
    }
}