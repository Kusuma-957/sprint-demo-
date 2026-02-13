package com.hotelManagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRevenueResponseDTO {
    private BigDecimal totalRevenue;
}
