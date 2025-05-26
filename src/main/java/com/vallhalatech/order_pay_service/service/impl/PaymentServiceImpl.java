

// PaymentServiceImpl.java
package com.vallhalatech.order_pay_service.service.impl;

import com.vallhalatech.order_pay_service.persistence.entities.Order;
import com.vallhalatech.order_pay_service.persistence.entities.Payment;
import com.vallhalatech.order_pay_service.persistence.entities.enums.PaymentStatus;
import com.vallhalatech.order_pay_service.persistence.repositories.IOrderRepository;
import com.vallhalatech.order_pay_service.persistence.repositories.IPaymentRepository;
import com.vallhalatech.order_pay_service.service.IPaymentService;
import com.vallhalatech.order_pay_service.utils.mappers.IPaymentMapper;
import com.vallhalatech.order_pay_service.web.dtos.request.CreatePaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.request.ProcessPaymentRequest;
import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final IPaymentMapper paymentMapper;

    @Override
    @Transactional
    public BaseResponse createPayment(CreatePaymentRequest request) {
        try {
            // Validar que la orden existe
            Optional<Order> orderOpt = orderRepository.findById(request.getOrderId());
            if (orderOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Orden no encontrada")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            Order order = orderOpt.get();

            // Validar que el monto no exceda el total de la orden
            if (request.getAmount().compareTo(order.getTotalAmount()) > 0) {
                return BaseResponse.builder()
                        .message("El monto del pago no puede exceder el total de la orden")
                        .success(false)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            // Crear el pago
            Payment payment = paymentMapper.toEntity(request);
            payment.setOrder(order);
            payment.setPaymentReference(generatePaymentReference());
            payment.setStatus(PaymentStatus.PENDING);

            Payment savedPayment = paymentRepository.save(payment);

            log.info("Payment created successfully: {}", savedPayment.getPaymentReference());

            return BaseResponse.builder()
                    .data(paymentMapper.toResponse(savedPayment))
                    .message("Pago creado exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.CREATED)
                    .build();

        } catch (Exception e) {
            log.error("Error creating payment", e);
            return BaseResponse.builder()
                    .message("Error al crear el pago: " + e.getMessage())
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    @Transactional
    public BaseResponse processPayment(ProcessPaymentRequest request) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByPaymentReference(request.getPaymentReference());
            if (paymentOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Pago no encontrado")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            Payment payment = paymentOpt.get();

            // Actualizar el estado del pago
            payment.setStatus(PaymentStatus.valueOf(request.getStatus().toUpperCase()));
            payment.setTransactionId(request.getTransactionId());
            payment.setGatewayResponse(request.getGatewayResponse());

            if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
                payment.setProcessedAt(LocalDateTime.now());
            }

            Payment updatedPayment = paymentRepository.save(payment);

            // Si el pago fue exitoso, actualizar el estado de la orden si está completamente pagada
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                Order order = payment.getOrder();
                if (order.isPaid()) {
                    order.setStatus(com.vallhalatech.order_pay_service.persistence.entities.enums.OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                }
            }

            log.info("Payment processed successfully: {} - Status: {}",
                    updatedPayment.getPaymentReference(), updatedPayment.getStatus());

            return BaseResponse.builder()
                    .data(paymentMapper.toResponse(updatedPayment))
                    .message("Pago procesado exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error processing payment: {}", request.getPaymentReference(), e);
            return BaseResponse.builder()
                    .message("Error al procesar el pago: " + e.getMessage())
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getPaymentById(Long paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Pago no encontrado")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            return BaseResponse.builder()
                    .data(paymentMapper.toResponse(paymentOpt.get()))
                    .message("Pago encontrado")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting payment by id: {}", paymentId, e);
            return BaseResponse.builder()
                    .message("Error al obtener el pago")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getPaymentByReference(String paymentReference) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByPaymentReference(paymentReference);
            if (paymentOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Pago no encontrado")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            return BaseResponse.builder()
                    .data(paymentMapper.toResponse(paymentOpt.get()))
                    .message("Pago encontrado")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting payment by reference: {}", paymentReference, e);
            return BaseResponse.builder()
                    .message("Error al obtener el pago")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getPaymentsByOrderId(Long orderId) {
        try {
            List<Payment> payments = paymentRepository.findByOrderId(orderId);

            List<com.vallhalatech.order_pay_service.web.dtos.response.PaymentSummaryResponse> paymentSummaries =
                    payments.stream()
                            .map(paymentMapper::toSummaryResponse)
                            .toList();

            return BaseResponse.builder()
                    .data(paymentSummaries)
                    .message("Pagos de la orden obtenidos exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting payments for order: {}", orderId, e);
            return BaseResponse.builder()
                    .message("Error al obtener los pagos de la orden")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public BaseResponse getPaymentsByUserId(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Payment> paymentsPage = paymentRepository.findByUserId(userId, pageable);

            List<com.vallhalatech.order_pay_service.web.dtos.response.PaymentSummaryResponse> paymentSummaries =
                    paymentsPage.getContent().stream()
                            .map(paymentMapper::toSummaryResponse)
                            .toList();

            return BaseResponse.builder()
                    .data(paymentSummaries)
                    .message("Pagos del usuario obtenidos exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error getting payments for user: {}", userId, e);
            return BaseResponse.builder()
                    .message("Error al obtener los pagos del usuario")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    @Transactional
    public BaseResponse refundPayment(String paymentReference, String reason) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByPaymentReference(paymentReference);
            if (paymentOpt.isEmpty()) {
                return BaseResponse.builder()
                        .message("Pago no encontrado")
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            }

            Payment payment = paymentOpt.get();

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                return BaseResponse.builder()
                        .message("Solo se pueden reembolsar pagos completados")
                        .success(false)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setGatewayResponse("Refund: " + reason);
            payment.setProcessedAt(LocalDateTime.now());

            Payment refundedPayment = paymentRepository.save(payment);

            log.info("Payment refunded successfully: {} - Reason: {}", paymentReference, reason);

            return BaseResponse.builder()
                    .data(paymentMapper.toResponse(refundedPayment))
                    .message("Pago reembolsado exitosamente")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("Error refunding payment: {}", paymentReference, e);
            return BaseResponse.builder()
                    .message("Error al reembolsar el pago")
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}