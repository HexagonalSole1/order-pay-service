package com.vallhalatech.order_pay_service.web.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessPaymentRequest {

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    @NotBlank(message = "Status is required")
    private String status; // PROCESSING, COMPLETED, FAILED, CANCELLED

    private String transactionId;

    private String gatewayResponse;
}
