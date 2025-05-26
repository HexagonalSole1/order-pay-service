package com.vallhalatech.order_pay_service.client.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItemResponse {
    private String productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private Date addedAt;
}
