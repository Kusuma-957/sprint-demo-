package com.hotelManagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomResponseDTO {
    private Integer roomId;
    private Integer hotelId;
    private Integer roomNumber;
    private Integer roomTypeId;
    private Boolean isAvailable;
}