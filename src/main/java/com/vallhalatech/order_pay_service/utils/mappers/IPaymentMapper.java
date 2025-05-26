// IPaymentMapper.java
package com.vallhalatech.order_pay_service.utils.mappers;

import com.vallhalatech.order_pay_service.persistence.entities.Payment;
import com.vallhalatech.order_pay_service.web.dtos.request.CreatePaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.PaymentResponse;
import com.vallhalatech.order_pay_service.web.dtos.response.PaymentSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface IPaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "paymentReference", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "stringToPaymentMethod")
    Payment toEntity(CreatePaymentRequest request);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "paymentStatusToString")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "paymentStatusToString")
    PaymentSummaryResponse toSummaryResponse(Payment payment);

    @Named("stringToPaymentMethod")
    default com.vallhalatech.order_pay_service.persistence.entities.enums.PaymentMethod stringToPaymentMethod(String method) {
        return method != null ?
                com.vallhalatech.order_pay_service.persistence.entities.enums.PaymentMethod.valueOf(method.toUpperCase()) :
                null;
    }

    @Named("paymentMethodToString")
    default String paymentMethodToString(com.vallhalatech.order_pay_service.persistence.entities.enums.PaymentMethod method) {
        return method != null ? method.name() : null;
    }

    @Named("paymentStatusToString")
    default String paymentStatusToString(com.vallhalatech.order_pay_service.persistence.entities.enums.PaymentStatus status) {
        return status != null ? status.name() : null;
    }
}
