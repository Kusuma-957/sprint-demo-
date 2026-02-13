
package com.hotelManagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReservationResponseDTO {

    private Integer reservationId;
    private Integer roomId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
