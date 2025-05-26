
// IPaymentRepository.java
package com.vallhalatech.order_pay_service.persistence.repositories;

import com.vallhalatech.order_pay_service.persistence.entities.Payment;
import com.vallhalatech.order_pay_service.persistence.entities.enums.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByTransactionId(String transactionId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.order.userId = :userId")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.order.userId = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = :method AND p.status = :status")
    Long countByPaymentMethodAndStatus(@Param("method") PaymentMethod method, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.status = :status")
    List<Payment> findByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           @Param("status") PaymentStatus status);

    boolean existsByPaymentReference(String paymentReference);

    boolean existsByTransactionId(String transactionId);
}