//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.PaymentCreateDTO;
//import com.hotelManagement.system.dto.PaymentResponseDTO;
//import com.hotelManagement.system.dto.PaymentRevenueResponseDTO;
//import com.hotelManagement.system.entity.Payment;
//import com.hotelManagement.system.entity.PaymentStatus;
//import com.hotelManagement.system.entity.Reservation;
//import com.hotelManagement.system.mapper.PaymentMapper;
//import com.hotelManagement.system.repository.PaymentRepository;
//import com.hotelManagement.system.repository.ReservationRepository;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    private final ReservationRepository reservationRepository;
//
//    // POST /api/payment/post
//    public PaymentResponseDTO create(PaymentCreateDTO dto) {
//
//        Reservation res = reservationRepository.findById(dto.getReservationId())
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + dto.getReservationId()));
//
//        Payment p = PaymentMapper.toEntity(dto);
//        p.setReservation(res);
//        p.setPaymentDate(LocalDate.now()); // per your entity
//
//        return PaymentMapper.toResponse(paymentRepository.save(p));
//    }
//
//    // GET /api/payment/{payment_id}
//    public PaymentResponseDTO get(Integer paymentId) {
//        Payment p = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
//        return PaymentMapper.toResponse(p);
//    }
//
//    // GET /api/payment/all
//    public List<PaymentResponseDTO> getAll() {
//        return PaymentMapper.toResponseList(paymentRepository.findAll());
//    }
//
//    // GET /api/payments/status/{status}
//    public List<PaymentResponseDTO> getByStatus(PaymentStatus statusRaw) {
//        return PaymentMapper.toResponseList(paymentRepository.findByPaymentStatus(statusRaw));
//    }
//
//    // GET /api/payments/total-revenue
//    public PaymentRevenueResponseDTO getTotalRevenue() {
//        return new PaymentRevenueResponseDTO(paymentRepository.sumTotalRevenuePaid());
//    }
//
//    // DELETE /api/payment/{payment_id}
//    public void delete(Integer paymentId) {
//        Payment p = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
//        paymentRepository.delete(p);
//    }
//}


package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.PaymentCreateDTO;
import com.hotelManagement.system.dto.PaymentResponseDTO;
import com.hotelManagement.system.dto.PaymentRevenueResponseDTO;
import com.hotelManagement.system.entity.Payment;
import com.hotelManagement.system.entity.PaymentStatus;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
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
    public void create(PaymentCreateDTO dto) {

        Reservation res = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation doesn't exist"));

        // ✅ ADDFAILS — prevent duplicate payment for same reservation & status
        if (paymentRepository.existsByReservation_ReservationIdAndPaymentStatus(dto.getReservationId(), dto.getPaymentStatus())) {
            throw new ConflictException("Payment already exist");
        }

        Payment p = PaymentMapper.toEntity(dto);
        p.setReservation(res);
        p.setPaymentDate(LocalDate.now());

        paymentRepository.save(p);
    }

    // GET /api/payment/{payment_id}
    public PaymentResponseDTO get(Integer paymentId) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment doesn't exist"));
        return PaymentMapper.toResponse(p);
    }

    // GET /api/payment/all
    public List<PaymentResponseDTO> getAll() {
        List<Payment> list = paymentRepository.findAll();
        if (list.isEmpty()) {
            throw new EmptyListException("Payment list is empty");
        }
        return PaymentMapper.toResponseList(list);
    }

    // GET /api/payments/status/{status}
    public List<PaymentResponseDTO> getByStatus(PaymentStatus status) {
        List<Payment> list = paymentRepository.findByPaymentStatus(status);
        if (list.isEmpty()) {
            throw new EmptyListException("Payment list is empty");
        }
        return PaymentMapper.toResponseList(list);
    }

    // GET /api/payments/total-revenue
    public PaymentRevenueResponseDTO getTotalRevenue() {
        return new PaymentRevenueResponseDTO(paymentRepository.sumTotalRevenuePaid());
    }

    // DELETE /api/payment/{payment_id}
    public void delete(Integer paymentId) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment doesn't exist"));
        paymentRepository.delete(p);
    }
}