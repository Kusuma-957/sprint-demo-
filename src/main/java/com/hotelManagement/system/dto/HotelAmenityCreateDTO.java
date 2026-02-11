package com.hotelManagement.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelAmenityCreateDTO {

    @NotNull
    private Integer hotelId;

    @NotNull
    private Integer amenityId;
}