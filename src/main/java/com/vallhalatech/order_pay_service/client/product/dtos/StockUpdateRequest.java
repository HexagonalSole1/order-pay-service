package com.vallhalatech.order_pay_service.client.product.dtos;
// StockUpdateRequest.java (para el cliente de productos)
import lombok.Data;

@Data
public class StockUpdateRequest {
    private Integer stock;

    public StockUpdateRequest(Integer stock) {
        this.stock = stock;
    }
}