package com.ezh.Inventory.sales.payment.controller;


import com.ezh.Inventory.sales.payment.dto.InvoicePaymentHistoryDto;
import com.ezh.Inventory.sales.payment.dto.InvoicePaymentSummaryDto;
import com.ezh.Inventory.sales.payment.dto.PaymentCreateDto;
import com.ezh.Inventory.sales.payment.dto.PaymentDto;
import com.ezh.Inventory.sales.payment.service.PaymentService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> recordPayment(@RequestBody PaymentCreateDto paymentCreateDto) throws CommonException {
        log.info("Entering recordPayment with : {}", paymentCreateDto);
        CommonResponse response = paymentService.recordPayment(paymentCreateDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "Payment recorded successfully");
    }

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<PaymentDto>> getAllPayments(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) throws CommonException {
        log.info("Fetching payments page: {}, size: {}", page, size);
        Page<PaymentDto> response = paymentService.getAllPayments(page, size);
        return ResponseResource.success(HttpStatus.OK, response, "Payments fetched successfully");
    }

    @GetMapping(value = "/{invoiceId}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<InvoicePaymentSummaryDto> getPaymentSummary(@PathVariable Long invoiceId) throws CommonException {
        log.info("Entering getPaymentSummary with : {}", invoiceId);
        InvoicePaymentSummaryDto response = paymentService.getInvoicePaymentSummary(invoiceId);
        return ResponseResource.success(HttpStatus.CREATED, response, "Payment summary fetched successfully");
    }

    @GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<List<InvoicePaymentHistoryDto>> getPaymentDetails(@PathVariable Long invoiceId) throws CommonException {
        log.info("Entering getPaymentDetails with ID : {}", invoiceId);
        List<InvoicePaymentHistoryDto> response = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ResponseResource.success(HttpStatus.CREATED, response, "Payment recorded successfully");
    }
}
