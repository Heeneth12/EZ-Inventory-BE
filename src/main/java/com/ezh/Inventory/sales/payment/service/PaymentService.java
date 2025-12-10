package com.ezh.Inventory.sales.payment.service;

import com.ezh.Inventory.sales.payment.dto.PaymentCreateDto;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;

public interface PaymentService {

    CommonResponse recordPayment(PaymentCreateDto dto) throws CommonException;
}
