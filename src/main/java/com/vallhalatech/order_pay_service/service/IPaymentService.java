package com.vallhalatech.order_pay_service.service;

import com.vallhalatech.order_pay_service.web.dtos.request.CreatePaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.ProcessPaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;

public interface IPaymentService {
    BaseResponse createPayment(CreatePaymentRequest request);
    BaseResponse processPayment(ProcessPaymentRequest request);
    BaseResponse getPaymentById(Long paymentId);
    BaseResponse getPaymentByReference(String paymentReference);
    BaseResponse getPaymentsByOrderId(Long orderId);
    BaseResponse getPaymentsByUserId(Long userId, int page, int size);
    BaseResponse refundPayment(String paymentReference, String reason);
}
