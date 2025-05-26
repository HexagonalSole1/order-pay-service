package com.vallhalatech.order_pay_service.web.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentSummaryResponse {
    private Long id;
    private String paymentReference;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
}
