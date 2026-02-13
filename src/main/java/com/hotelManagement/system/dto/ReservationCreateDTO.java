package com.hotelManagement.system.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationCreateDTO {

    @NotNull
    private Integer roomId;

    @NotBlank
    @Size(max = 255)
    private String guestName;

    @NotBlank
    @Email
    private String guestEmail;

    @NotBlank
    @Size(max = 20)
    private String guestPhone;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;
}