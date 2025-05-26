package com.vallhalatech.order_pay_service.utils.mappers;
// IOrderItemMapper.java

import com.vallhalatech.order_pay_service.persistence.entities.OrderItem;
import com.vallhalatech.order_pay_service.web.dtos.request.OrderItemRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface IOrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItem toEntity(OrderItemRequest request);

    OrderItemResponse toResponse(OrderItem orderItem);
}
