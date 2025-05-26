
// OrderController.java
package com.vallhalatech.order_pay_service.web.controller;

import com.vallhalatech.order_pay_service.service.IOrderService;
import com.vallhalatech.order_pay_service.web.dtos.request.CreateOrderRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.OrderSearchRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.UpdateOrderStatusRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        BaseResponse response = orderService.createOrder(request);
        return response.buildResponseEntity();
    }

    @PostMapping("/from-cart/{userId}")
    public ResponseEntity<BaseResponse> createOrderFromCart(@PathVariable String userId) {
        BaseResponse response = orderService.createOrderFromCart(userId);
        return response.buildResponseEntity();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse> getOrderById(@PathVariable Long orderId) {
        BaseResponse response = orderService.getOrderById(orderId);
        return response.buildResponseEntity();
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<BaseResponse> getOrderByNumber(@PathVariable String orderNumber) {
        BaseResponse response = orderService.getOrderByNumber(orderNumber);
        return response.buildResponseEntity();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BaseResponse response = orderService.getOrdersByUserId(userId, page, size);
        return response.buildResponseEntity();
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse> searchOrders(@RequestBody OrderSearchRequest searchRequest) {
        BaseResponse response = orderService.searchOrders(searchRequest);
        return response.buildResponseEntity();
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<BaseResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        BaseResponse response = orderService.updateOrderStatus(orderId, request);
        return response.buildResponseEntity();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false, defaultValue = "Usuario canceló la orden") String reason) {
        BaseResponse response = orderService.cancelOrder(orderId, reason);
        return response.buildResponseEntity();
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<BaseResponse> getOrderStats(@PathVariable Long userId) {
        BaseResponse response = orderService.getOrderStats(userId);
        return response.buildResponseEntity();
    }
}