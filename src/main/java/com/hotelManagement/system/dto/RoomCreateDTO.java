package com.hotelManagement.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateDTO {

//    @NotNull
//    private Integer hotelId;

    @NotNull
    private Integer roomNumber;

    @NotNull
    private Integer roomTypeId;

    private Boolean isAvailable = true;
}