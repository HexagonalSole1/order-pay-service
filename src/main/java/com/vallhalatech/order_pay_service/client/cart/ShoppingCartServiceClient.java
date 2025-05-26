package com.vallhalatech.order_pay_service.client.cart;
// ShoppingCartServiceClient.java

import com.vallhalatech.order_pay_service.client.dtos.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-cart-service")
public interface ShoppingCartServiceClient {

    @GetMapping("/cart/{userId}")
    ResponseEntity<BaseResponse> getCartByUserId(@PathVariable("userId") String userId);

    @DeleteMapping("/cart/{userId}")
    ResponseEntity<BaseResponse> clearCart(@PathVariable("userId") String userId);
}
