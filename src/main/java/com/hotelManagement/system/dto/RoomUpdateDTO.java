package com.hotelManagement.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateDTO {

    @NotNull
    private Integer roomNumber;

    @NotNull
    private Integer roomTypeId;

    @NotNull
    private Boolean isAvailable;
}