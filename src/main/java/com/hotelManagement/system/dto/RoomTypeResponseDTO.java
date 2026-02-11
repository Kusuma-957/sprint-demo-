package com.hotelManagement.system.dto;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeResponseDTO {
    private Integer roomTypeId;
    private String typeName;
    private Integer maxOccupancy;
    private BigDecimal pricePerNight;
    private String description;
}