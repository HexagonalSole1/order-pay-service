package com.vallhalatech.order_pay_service.client.product;

import com.vallhalatech.order_pay_service.client.dtos.BaseResponse;
import com.vallhalatech.order_pay_service.client.product.dtos.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/products/{productId}")
    ResponseEntity<BaseResponse> getProductById(@PathVariable("productId") Long productId);

    @PatchMapping("/products/{productId}/stock")
    ResponseEntity<BaseResponse> updateStock(@PathVariable("productId") Long productId,
                                             @RequestBody StockUpdateRequest request);
}