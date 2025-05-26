package com.vallhalatech.order_pay_service.web.dtos.response;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class OrderSummaryResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private Integer totalItems;
    private Boolean isPaid;
    private LocalDateTime createdAt;
}
