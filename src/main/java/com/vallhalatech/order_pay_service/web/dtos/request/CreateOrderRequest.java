// CreateOrderRequest.java
package com.vallhalatech.order_pay_service.web.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    @DecimalMin(value = "0.0", inclusive = false, message = "Tax amount must be positive")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping amount cannot be negative")
    private BigDecimal shippingAmount;

    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;

    @Size(max = 500, message = "Billing address cannot exceed 500 characters")
    private String billingAddress;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private String paymentMethod; // Para crear pago inmediatamente si se especifica
}
