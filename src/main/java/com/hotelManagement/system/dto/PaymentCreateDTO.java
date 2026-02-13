package com.hotelManagement.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentCreateDTO {

    @NotNull
    private Integer reservationId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "amount must be > 0")
    private BigDecimal amount;

    // e.g., PAID, PENDING, FAILED (string per your entity)
    @NotNull
    private String paymentStatus;

    // paymentDate is NOT sent by client; service will set LocalDate.now()
}