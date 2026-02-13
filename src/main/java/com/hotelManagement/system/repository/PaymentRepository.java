package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // Case-insensitive filter by status
    @Query("select p from Payment p where upper(p.paymentStatus) = upper(:status)")
    List<Payment> findByStatus(@Param("status") String status);

    // Total revenue = sum of amount for PAID payments
    @Query("select coalesce(sum(p.amount), 0) from Payment p where upper(p.paymentStatus) = 'PAID'")
    BigDecimal sumTotalRevenuePaid();
}
