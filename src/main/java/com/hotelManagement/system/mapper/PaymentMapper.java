package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.PaymentCreateDTO;

import com.hotelManagement.system.dto.PaymentResponseDTO;
import com.hotelManagement.system.entity.Payment;

import java.util.List;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static Payment toEntity(PaymentCreateDTO dto) {
        Payment p = new Payment();
        p.setAmount(dto.getAmount());
        p.setPaymentStatus(dto.getPaymentStatus());
        // reservation & paymentDate will be set in service
        return p;
    }

    public static PaymentResponseDTO toResponse(Payment p) {
        Integer reservationId = (p.getReservation() != null)
                ? p.getReservation().getReservationId()
                : null;

        return new PaymentResponseDTO(
                p.getPaymentId(),
                reservationId,
                p.getAmount(),
                p.getPaymentStatus(),
                p.getPaymentDate()
        );
    }

    public static List<PaymentResponseDTO> toResponseList(List<Payment> list) {
        return list.stream().map(PaymentMapper::toResponse).toList();
    }
}