
package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.ReservationCreateDTO;
import com.hotelManagement.system.dto.ReservationResponseDTO;
import com.hotelManagement.system.dto.ReservationUpdateDTO;
import com.hotelManagement.system.entity.Reservation;

import java.util.List;

public final class ReservationMapper {

    private ReservationMapper() {}

    public static Reservation toEntity(ReservationCreateDTO dto) {
        Reservation r = new Reservation();
        r.setGuestName(dto.getGuestName());
        r.setGuestEmail(dto.getGuestEmail());
        r.setGuestPhone(dto.getGuestPhone());
        r.setCheckInDate(dto.getCheckInDate());
        r.setCheckOutDate(dto.getCheckOutDate());
        return r;
    }

    public static void updateEntity(ReservationUpdateDTO dto, Reservation entity) {
        entity.setGuestName(dto.getGuestName());
        entity.setGuestEmail(dto.getGuestEmail());
        entity.setGuestPhone(dto.getGuestPhone());
        entity.setCheckInDate(dto.getCheckInDate());
        entity.setCheckOutDate(dto.getCheckOutDate());
    }

    public static ReservationResponseDTO toResponse(Reservation r) {
        Integer roomId = (r.getRoom() != null) ? r.getRoom().getRoomId() : null;
        return new ReservationResponseDTO(
                r.getReservationId(),
                roomId,
                r.getGuestName(),
                r.getGuestEmail(),
                r.getGuestPhone(),
                r.getCheckInDate(),
                r.getCheckOutDate()
        );
    }

    public static List<ReservationResponseDTO> toResponseList(List<Reservation> list) {
        return list.stream().map(ReservationMapper::toResponse).toList();
    }
}