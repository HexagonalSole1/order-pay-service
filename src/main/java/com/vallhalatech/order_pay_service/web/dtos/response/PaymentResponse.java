package com.vallhalatech.order_pay_service.web.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentReference;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String gatewayResponse;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
