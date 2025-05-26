package com.vallhalatech.order_pay_service.utils.mappers;
// IOrderMapper.java

import com.vallhalatech.order_pay_service.persistence.entities.Order;
import com.vallhalatech.order_pay_service.web.dtos.request.CreateOrderRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.CreateOrderResponse;
import com.vallhalatech.order_pay_service.web.dtos.response.OrderResponse;
import com.vallhalatech.order_pay_service.web.dtos.response.OrderSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
@Mapper(componentModel = "spring", uses = {IOrderItemMapper.class, IPaymentMapper.class})
public interface IOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(CreateOrderRequest request);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "calculateSubtotal")
    @Mapping(target = "isPaid", source = ".", qualifiedByName = "checkIfPaid")
    @Mapping(target = "totalItems", source = "orderItems", qualifiedByName = "calculateTotalItems")
    OrderResponse toResponse(Order order);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "isPaid", source = ".", qualifiedByName = "checkIfPaid")
    @Mapping(target = "totalItems", source = "orderItems", qualifiedByName = "calculateTotalItems")
    OrderSummaryResponse toSummaryResponse(Order order);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    CreateOrderResponse toCreateResponse(Order order);

    @Named("statusToString")
    default String statusToString(com.vallhalatech.order_pay_service.persistence.entities.enums.OrderStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(Order order) {
        return order.calculateSubtotal();
    }

    @Named("checkIfPaid")
    default Boolean checkIfPaid(Order order) {
        return order.isPaid();
    }

    @Named("calculateTotalItems")
    default Integer calculateTotalItems(java.util.List<com.vallhalatech.order_pay_service.persistence.entities.OrderItem> orderItems) {
        return orderItems != null ?
                orderItems.stream()
                        .mapToInt(com.vallhalatech.order_pay_service.persistence.entities.OrderItem::getQuantity)
                        .sum() : 0;
    }
}
