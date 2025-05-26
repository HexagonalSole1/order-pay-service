package com.vallhalatech.order_pay_service.service.impl;


// OrderServiceImpl.java

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vallhalatech.order_pay_service.client.auth.AuthServiceClient;
import com.vallhalatech.order_pay_service.client.cart.ShoppingCartServiceClient;
import com.vallhalatech.order_pay_service.client.dtos.CartItemResponse;
import com.vallhalatech.order_pay_service.client.dtos.CartResponse;
import com.vallhalatech.order_pay_service.client.dtos.InfoUserResponse;
import com.vallhalatech.order_pay_service.client.product.ProductServiceClient;
import com.vallhalatech.order_pay_service.persistence.entities.Order;
import com.vallhalatech.order_pay_service.persistence.entities.OrderItem;
import com.vallhalatech.order_pay_service.persistence.entities.enums.*;
import com.vallhalatech.order_pay_service.persistence.repositories.IOrderRepository;
import com.vallhalatech.order_pay_service.service.IOrderService;
import com.vallhalatech.order_pay_service.utils.mappers.IOrderItemMapper;
import com.vallhalatech.order_pay_service.utils.mappers.IOrderMapper;
import com.vallhalatech.order_pay_service.web.dtos.request.CreateOrderRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.OrderSearchRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.UpdateOrderStatusRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;
import com.vallhalatech.order_pay_service.web.dtos.response.OrderStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderMapper orderMapper;
    private final IOrderItemMapper orderItemMapper;
    private final AuthServiceClient authServiceClient;
    private final ProductServiceClient productServiceClient;
    private final ShoppingCartServiceClient cartServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public BaseResponse createOrder(CreateOrderRequest request) {
        try {
            // Validar que el usuario existe
            ResponseEntity<com.vallhalatech.order_pay_service.client.dtos.BaseResponse> userResponse =
                    authServiceClient.findUserByEmail(request.getUserEmail());

            if (!userResponse.getStatusCode().is2xxSuccessful() ||
                    userResponse.getBody() == null ||
                    !Boolean.TRUE.equals(userResponse.getBody().getSuccess())) {
                return BaseResponse.builder()
                        .message("Usuario no encontrado")
                        .success(false)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            // Crear la orden
            Order order = orderMapper.toEntity(request);
            order.setOrderNumber(generateOrderNumber());
            order.setStatus(OrderStatus.PENDING);

            // Crear los items de la orden
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;

            for (var itemRequest : request.getItems()) {
                OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
                orderItem.setOrder(order);
                orderItems.add(orderItem);

                subtotal = subtotal.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }

            order.setOrderItems(orderItems);

            // Calcular total
            BigDecimal total = subtotal;
            if (request.getTaxAmount() != null) {
                total = total.add(request.getTaxAmount());
            }
            if (request.getShippingAmount() != null) {
                total = total.add(request.getShippingAmount());
            }
            order.setTotalAmount(total);

            // Guardar la orden
            Order savedOrder = orderRepository.save(order);

            log.info("Order created successfully: {}", savedOrder.getOrderNumber());

            return BaseResponse.builder()
                    .data(orderMapper.toCreateResponse(savedOrder))
                    .message("Orden creada exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.CREATED)
                    .build();

        } catch (Exception e) {
            log.error("Error creating order", e);
            return BaseResponse.builder()
                    .message("Error al crear la orden: " + e.getMessage())
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getOrderById(Long orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Orden no encontrada")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            return BaseResponse.builder()
                    .data(orderMapper.toResponse(orderOpt.get()))
                    .message("Orden encontrada")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting order by id: {}", orderId, e);
            return BaseResponse.builder()
                    .message("Error al obtener la orden")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getOrderByNumber(String orderNumber) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            if (orderOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Orden no encontrada")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            return BaseResponse.builder()
                    .data(orderMapper.toResponse(orderOpt.get()))
                    .message("Orden encontrada")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting order by number: {}", orderNumber, e);
            return BaseResponse.builder()
                    .message("Error al obtener la orden")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getOrdersByUserId(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);

            List<com.vallhalatech.order_pay_service.web.dtos.response.OrderSummaryResponse> orderSummaries =
                    ordersPage.getContent().stream()
                            .map(orderMapper::toSummaryResponse)
                            .toList();

            return BaseResponse.builder()
                    .data(orderSummaries)
                    .message("Órdenes del usuario obtenidas exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting orders for user: {}", userId, e);
            return BaseResponse.builder()
                    .message("Error al obtener las órdenes del usuario")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse searchOrders(OrderSearchRequest searchRequest) {
        // Implementar lógica de búsqueda compleja según los criterios
        // Por simplicidad, delegamos a getOrdersByUserId si se especifica userId
        if (searchRequest.getUserId() != null) {
            return getOrdersByUserId(searchRequest.getUserId(), searchRequest.getPage(), searchRequest.getSize());
        }

        return BaseResponse.builder()
                .message("Función de búsqueda no implementada completamente")
                .success(false)
                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @Override
    @Transactional
    public BaseResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Orden no encontrada")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            Order order = orderOpt.get();
            order.setStatus(OrderStatus.valueOf(request.getStatus().toUpperCase()));
            if (request.getNotes() != null) {
                order.setNotes(request.getNotes());
            }

            Order updatedOrder = orderRepository.save(order);

            return BaseResponse.builder()
                    .data(orderMapper.toResponse(updatedOrder))
                    .message("Estado de la orden actualizado exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error updating order status: {}", orderId, e);
            return BaseResponse.builder()
                    .message("Error al actualizar el estado de la orden")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    @Transactional
    public BaseResponse cancelOrder(Long orderId, String reason) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus("CANCELLED");
        request.setNotes("Orden cancelada: " + reason);

        return updateOrderStatus(orderId, request);
    }

    @Override
    public BaseResponse getOrderStats(Long userId) {
        try {
            Long totalOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.DELIVERED) +
                    orderRepository.countByUserIdAndStatus(userId, OrderStatus.PENDING) +
                    orderRepository.countByUserIdAndStatus(userId, OrderStatus.PROCESSING);

            Long pendingOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.PENDING);
            Long completedOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.DELIVERED);
            Long cancelledOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.CANCELLED);

            OrderStatsResponse stats = new OrderStatsResponse();
            stats.setTotalOrders(totalOrders);
            stats.setPendingOrders(pendingOrders);
            stats.setCompletedOrders(completedOrders);
            stats.setCancelledOrders(cancelledOrders);

            return BaseResponse.builder()
                    .data(stats)
                    .message("Estadísticas obtenidas exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting order stats for user: {}", userId, e);
            return BaseResponse.builder()
                    .message("Error al obtener estadísticas")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    @Transactional
    public BaseResponse createOrderFromCart(String userId) {
        try {
            // Obtener el carrito del usuario
            ResponseEntity<com.vallhalatech.order_pay_service.client.dtos.BaseResponse> cartResponse =
                    cartServiceClient.getCartByUserId(userId);

            if (!cartResponse.getStatusCode().is2xxSuccessful() ||
                    cartResponse.getBody() == null ||
                    !Boolean.TRUE.equals(cartResponse.getBody().getSuccess())) {
                return BaseResponse.builder()
                        .message("No se pudo obtener el carrito del usuario")
                        .success(false)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            CartResponse cart = objectMapper.convertValue(cartResponse.getBody().getData(), CartResponse.class);

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return BaseResponse.builder()
                        .message("El carrito está vacío")
                        .success(false)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            // Convertir items del carrito a CreateOrderRequest
            CreateOrderRequest orderRequest = new CreateOrderRequest();
            orderRequest.setUserId(Long.valueOf(userId));

            List<com.vallhalatech.order_pay_service.web.dtos.request.OrderItemRequest> orderItems = new ArrayList<>();
            for (CartItemResponse cartItem : cart.getItems().values()) {
                com.vallhalatech.order_pay_service.web.dtos.request.OrderItemRequest orderItem =
                        new com.vallhalatech.order_pay_service.web.dtos.request.OrderItemRequest();
                orderItem.setProductId(Long.valueOf(cartItem.getProductId()));
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setProductImageUrl(cartItem.getImageUrl());
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItems.add(orderItem);
            }

            orderRequest.setItems(orderItems);

            // Crear la orden
            BaseResponse createOrderResponse = createOrder(orderRequest);

            // Si la orden se creó exitosamente, limpiar el carrito
            if (Boolean.TRUE.equals(createOrderResponse.getSuccess())) {
                cartServiceClient.clearCart(userId);
            }

            return createOrderResponse;

        } catch (Exception e) {
            log.error("Error creating order from cart for user: {}", userId, e);
            return BaseResponse.builder()
                    .message("Error al crear orden desde el carrito")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
