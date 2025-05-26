
// PaymentController.java
package com.vallhalatech.order_pay_service.web.controller;

import com.vallhalatech.order_pay_service.service.IPaymentService;
import com.vallhalatech.order_pay_service.web.dtos.request.CreatePaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.ProcessPaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping
    public ResponseEntity<BaseResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        BaseResponse response = paymentService.createPayment(request);
        return response.buildResponseEntity();
    }

    @PostMapping("/process")
    public ResponseEntity<BaseResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        BaseResponse response = paymentService.processPayment(request);
        return response.buildResponseEntity();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<BaseResponse> getPaymentById(@PathVariable Long paymentId) {
        BaseResponse response = paymentService.getPaymentById(paymentId);
        return response.buildResponseEntity();
    }

    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<BaseResponse> getPaymentByReference(@PathVariable String paymentReference) {
        BaseResponse response = paymentService.getPaymentByReference(paymentReference);
        return response.buildResponseEntity();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<BaseResponse> getPaymentsByOrderId(@PathVariable Long orderId) {
        BaseResponse response = paymentService.getPaymentsByOrderId(orderId);
        return response.buildResponseEntity();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BaseResponse response = paymentService.getPaymentsByUserId(userId, page, size);
        return response.buildResponseEntity();
    }

    @PostMapping("/refund/{paymentReference}")
    public ResponseEntity<BaseResponse> refundPayment(
            @PathVariable String paymentReference,
            @RequestParam String reason) {
        BaseResponse response = paymentService.refundPayment(paymentReference, reason);
        return response.buildResponseEntity();
    }
}