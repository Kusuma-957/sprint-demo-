package com.hotelManagement.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAmenityCreateDTO {

    @NotNull
    private Integer roomId;

    @NotNull
    private Integer amenityId;
}