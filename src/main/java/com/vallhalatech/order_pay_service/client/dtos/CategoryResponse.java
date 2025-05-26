package com.vallhalatech.order_pay_service.client.dtos;


import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}