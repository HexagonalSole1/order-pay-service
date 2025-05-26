package com.vallhalatech.order_pay_service.web.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderResponse {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String message;
}
