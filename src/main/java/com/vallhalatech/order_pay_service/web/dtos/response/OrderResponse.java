package com.vallhalatech.order_pay_service.web.dtos.response;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private String status;
    private String shippingAddress;
    private String billingAddress;
    private String notes;
    private List<OrderItemResponse> orderItems;
    private List<PaymentResponse> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Campos calculados
    private BigDecimal subtotal;
    private Boolean isPaid;
    private Integer totalItems;
}
