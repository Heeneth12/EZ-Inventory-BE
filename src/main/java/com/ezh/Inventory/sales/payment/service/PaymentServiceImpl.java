package com.ezh.Inventory.sales.payment.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import com.ezh.Inventory.sales.invoice.repository.InvoiceRepository;
import com.ezh.Inventory.sales.payment.dto.*;
import com.ezh.Inventory.sales.payment.entity.Payment;
import com.ezh.Inventory.sales.payment.entity.PaymentAllocation;
import com.ezh.Inventory.sales.payment.entity.PaymentStatus;
import com.ezh.Inventory.sales.payment.repository.PaymentAllocationRepository;
import com.ezh.Inventory.sales.payment.repository.PaymentRepository;
import com.ezh.Inventory.utils.UserContextUtil;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public CommonResponse recordPayment(PaymentCreateDto dto) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        Contact customer = contactRepository.findByIdAndTenantId(dto.getCustomerId(), tenantId)
                .orElseThrow(() -> new CommonException("Customer not found", HttpStatus.NOT_FOUND));

        // 1. Create Payment Header (Source of Funds)
        Payment payment = Payment.builder()
                .tenantId(tenantId)
                .paymentNumber("PAY-" + System.currentTimeMillis())
                .customer(customer)
                .paymentDate(new Date())
                .amount(dto.getTotalAmount())
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(dto.getPaymentMethod())
                .referenceNumber(dto.getReferenceNumber())
                .remarks(dto.getRemarks())
                .allocatedAmount(BigDecimal.ZERO)
                .unallocatedAmount(dto.getTotalAmount()) // Initially all unallocated
                .allocations(new ArrayList<>())
                .build();

        // Save strictly to generate ID
        payment = paymentRepository.save(payment);

        // 2. Process Allocations (If any)
        if (dto.getAllocations() != null && !dto.getAllocations().isEmpty()) {
            processAllocations(payment, dto.getAllocations(), tenantId);
        }

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .id(payment.getId().toString())
                .message("Payment Recorded Successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoicePaymentSummaryDto getInvoicePaymentSummary(Long invoiceId) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        // 1. Fetch Invoice
        Invoice invoice = invoiceRepository.findByIdAndTenantId(invoiceId, tenantId)
                .orElseThrow(() -> new CommonException("Invoice not found", HttpStatus.NOT_FOUND));

        // 2. Fetch History
        List<PaymentAllocation> history = allocationRepository
                .findByInvoiceIdAndTenantIdOrderByAllocationDateDesc(invoiceId, tenantId);

        // 3. Map History Items (FIXED: Accessing Parent Payment for details)
        List<InvoicePaymentHistoryDto> historyDtos = history.stream()
                .map(alloc -> {
                    Payment parentPayment = alloc.getPayment(); // Get the header
                    return InvoicePaymentHistoryDto.builder()
                            .id(alloc.getId())
                            .paymentId(parentPayment.getId())
                            .paymentNumber(parentPayment.getPaymentNumber())
                            .paymentDate(parentPayment.getPaymentDate()) // Use Receipt Date
                            .amount(alloc.getAllocatedAmount())
                            // FIX: Get details from Parent Payment, not Allocation
                            .method(parentPayment.getPaymentMethod())
                            .referenceNumber(parentPayment.getReferenceNumber())
                            .remarks(parentPayment.getRemarks())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Build Summary
        return InvoicePaymentSummaryDto.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(invoice.getCustomer().getId())
                .customerName(invoice.getCustomer().getName())
                .invoiceDate(invoice.getInvoiceDate())
                .status(invoice.getStatus())
                .grandTotal(invoice.getGrandTotal())
                .totalPaid(invoice.getAmountPaid())
                .balanceDue(invoice.getBalance())
                .paymentHistory(historyDtos)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<InvoicePaymentHistoryDto> getPaymentsByInvoiceId(Long invoiceId) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        // 1. Fetch Allocations
        List<PaymentAllocation> allocations = allocationRepository
                .findByInvoiceIdAndTenantIdOrderByAllocationDateDesc(invoiceId, tenantId);

        // 2. Map to DTO (FIXED)
        return allocations.stream()
                .map(alloc -> {
                    Payment parentPayment = alloc.getPayment(); // Get the header
                    return InvoicePaymentHistoryDto.builder()
                            .id(alloc.getId())
                            .paymentId(parentPayment.getId())
                            .paymentNumber(parentPayment.getPaymentNumber())
                            .paymentDate(parentPayment.getPaymentDate())
                            .amount(alloc.getAllocatedAmount())
                            // FIX: Get details from Parent Payment
                            .method(parentPayment.getPaymentMethod())
                            .referenceNumber(parentPayment.getReferenceNumber())
                            .remarks(parentPayment.getRemarks())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Integer page, Integer size) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Payment> payments = paymentRepository.findByTenantId(tenantId, pageable);

        return payments.map(this::mapToDto);
    }


    private void processAllocations(Payment payment, List<PaymentAllocationDto> allocDtos, Long tenantId) {
        BigDecimal totalAllocatedNow = BigDecimal.ZERO;

        for (PaymentAllocationDto allocDto : allocDtos) {
            Invoice invoice = invoiceRepository.findByIdAndTenantId(allocDto.getInvoiceId(), tenantId)
                    .orElseThrow(() -> new CommonException("Invoice " + allocDto.getInvoiceId() + " not found", HttpStatus.BAD_REQUEST));

            // A. Validate: Don't overpay the invoice
            if (allocDto.getAmountToPay().compareTo(invoice.getBalance()) > 0) {
                throw new CommonException("Amount " + allocDto.getAmountToPay() + " exceeds balance for Invoice " + invoice.getInvoiceNumber(), HttpStatus.BAD_REQUEST);
            }

            // B. Create Allocation Record
            PaymentAllocation allocation = PaymentAllocation.builder()
                    .tenantId(tenantId)
                    .payment(payment)
                    .invoice(invoice)
                    .allocatedAmount(allocDto.getAmountToPay())
                    .allocationDate(new Date())
                    .build();

            allocationRepository.save(allocation);

            // C. Update Invoice Balance & Status
            updateInvoiceBalance(invoice, allocDto.getAmountToPay());

            totalAllocatedNow = totalAllocatedNow.add(allocDto.getAmountToPay());
        }

        // D. Update Payment Header Totals
        payment.setAllocatedAmount(payment.getAllocatedAmount().add(totalAllocatedNow));
        payment.setUnallocatedAmount(payment.getAmount().subtract(payment.getAllocatedAmount()));

        // Validation: Did user try to allocate more than the check amount?
        if (payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Allocation sum exceeds Total Payment Amount");
        }

        paymentRepository.save(payment);
    }

    private void updateInvoiceBalance(Invoice invoice, BigDecimal paidAmount) {
        BigDecimal newPaid = invoice.getAmountPaid().add(paidAmount);
        BigDecimal newBalance = invoice.getGrandTotal().subtract(newPaid);

        invoice.setAmountPaid(newPaid);
        invoice.setBalance(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceRepository.save(invoice);
    }

    private PaymentDto mapToDto(Payment payment) {
        if (payment == null) return null;

        return PaymentDto.builder()
                .id(payment.getId())
                .tenantId(payment.getTenantId())
                .paymentNumber(payment.getPaymentNumber())
                .customerId(payment.getCustomer().getId())
                .customerName(payment.getCustomer().getName())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .referenceNumber(payment.getReferenceNumber())
                .bankName(payment.getBankName())
                .remarks(payment.getRemarks())
                .allocatedAmount(payment.getAllocatedAmount())
                .unallocatedAmount(payment.getUnallocatedAmount())
                .build();
    }
}