package com.vallhalatech.order_pay_service.client.dtos;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class CartResponse {
    private String id;
    private String userId;
    private Map<String, CartItemResponse> items;
    private Date createdAt;
    private Date updatedAt;
}