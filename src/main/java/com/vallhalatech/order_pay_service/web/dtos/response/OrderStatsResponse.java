package com.vallhalatech.order_pay_service.web.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatsResponse {
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
}
