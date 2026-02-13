package com.hotelManagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponseDTO {

    private Integer paymentId;
    private Integer reservationId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDate paymentDate;
}