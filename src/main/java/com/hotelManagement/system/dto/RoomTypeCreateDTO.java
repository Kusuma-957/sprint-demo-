package com.hotelManagement.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomTypeCreateDTO {

    @NotBlank
    private String typeName;

    @NotNull
    private Integer maxOccupancy;

    @NotNull
    private BigDecimal pricePerNight;  // FIXED

    private String description;
}