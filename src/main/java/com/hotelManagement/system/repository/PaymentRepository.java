package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Payment;
import com.hotelManagement.system.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByPaymentStatus(PaymentStatus status);

    // Total revenue = sum of amount for PAID payments
    // Safer enum comparison than upper(...)
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentStatus = com.hotelManagement.system.entity.PaymentStatus.Paid")
    BigDecimal sumTotalRevenuePaid();

    // ✅ Duplicate protection for POST:
    boolean existsByReservation_ReservationIdAndPaymentStatus(Integer reservationId, PaymentStatus paymentStatus);

    // (Optional) If your rule is unique per reservation + amount:
    // boolean existsByReservation_ReservationIdAndAmount(Integer reservationId, BigDecimal amount);

    // (Optional) If your rule is unique per reservation + amount + status:
    // boolean existsByReservation_ReservationIdAndAmountAndPaymentStatus(Integer reservationId, BigDecimal amount, PaymentStatus status);
}