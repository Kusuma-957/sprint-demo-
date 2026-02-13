package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.PaymentCreateDTO;
import com.hotelManagement.system.dto.PaymentResponseDTO;
import com.hotelManagement.system.dto.PaymentRevenueResponseDTO;
import com.hotelManagement.system.entity.Payment;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.mapper.PaymentMapper;
import com.hotelManagement.system.repository.PaymentRepository;
import com.hotelManagement.system.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    // POST /api/payment/post
    public PaymentResponseDTO create(PaymentCreateDTO dto) {

        Reservation res = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + dto.getReservationId()));

        Payment p = PaymentMapper.toEntity(dto);
        p.setReservation(res);
        p.setPaymentDate(LocalDate.now()); // per your entity

        return PaymentMapper.toResponse(paymentRepository.save(p));
    }

    // GET /api/payment/{payment_id}
    public PaymentResponseDTO get(Integer paymentId) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        return PaymentMapper.toResponse(p);
    }

    // GET /api/payment/all
    public List<PaymentResponseDTO> getAll() {
        return PaymentMapper.toResponseList(paymentRepository.findAll());
    }

    // GET /api/payments/status/{status}
    public List<PaymentResponseDTO> getByStatus(String statusRaw) {
        if (statusRaw == null || statusRaw.isBlank()) {
            throw new IllegalArgumentException("status cannot be empty");
        }
        String normalized = statusRaw.trim();
        return PaymentMapper.toResponseList(paymentRepository.findByStatus(normalized));
    }

    // GET /api/payments/total-revenue
    public PaymentRevenueResponseDTO getTotalRevenue() {
        return new PaymentRevenueResponseDTO(paymentRepository.sumTotalRevenuePaid());
    }

    // DELETE /api/payment/{payment_id}
    public void delete(Integer paymentId) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        paymentRepository.delete(p);
    }
}