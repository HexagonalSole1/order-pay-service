package com.vallhalatech.order_pay_service.web.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSearchRequest {

    private Long userId;

    private String status;

    private String orderNumber;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int page = 0;

    private int size = 10;
}
