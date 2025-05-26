package com.vallhalatech.order_pay_service.service;

import com.vallhalatech.order_pay_service.web.dtos.request.CreateOrderRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.OrderSearchRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.UpdateOrderStatusRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;

public interface IOrderService {
    BaseResponse createOrder(CreateOrderRequest request);
    BaseResponse getOrderById(Long orderId);
    BaseResponse getOrderByNumber(String orderNumber);
    BaseResponse getOrdersByUserId(Long userId, int page, int size);
    BaseResponse searchOrders(OrderSearchRequest searchRequest);
    BaseResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    BaseResponse cancelOrder(Long orderId, String reason);
    BaseResponse getOrderStats(Long userId);
    BaseResponse createOrderFromCart(String userId);
}
