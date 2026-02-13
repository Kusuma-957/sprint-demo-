package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.ReservationCreateDTO;
import com.hotelManagement.system.dto.ReservationResponseDTO;
import com.hotelManagement.system.dto.ReservationUpdateDTO;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.mapper.ReservationMapper;
import com.hotelManagement.system.repository.ReservationRepository;
import com.hotelManagement.system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationResponseDTO create(ReservationCreateDTO dto) {
        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.getRoomId()));

        // no overlap per room
        boolean overlap = reservationRepository.existsOverlap(
                room.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate());
        if (overlap) {
            throw new IllegalStateException("Room is not available for the selected date range");
        }

        Reservation res = ReservationMapper.toEntity(dto);
        res.setRoom(room);

        return ReservationMapper.toResponse(reservationRepository.save(res));
    }

    public ReservationResponseDTO update(Integer reservationId, ReservationUpdateDTO dto) {
        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // If you allow moving rooms on update, fetch room by dto.getRoomId() and set here.
        // For now, use existing res.getRoom().
        boolean overlap = reservationRepository.existsOverlapExcluding(
                res.getReservationId(),
                res.getRoom().getRoomId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );
        if (overlap) {
            throw new IllegalStateException("Room is not available for the selected date range");
        }

        ReservationMapper.updateEntity(dto, res);
        return ReservationMapper.toResponse(reservationRepository.save(res));
    }

    public ReservationResponseDTO get(Integer reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        return ReservationMapper.toResponse(res);
    }

    public List<ReservationResponseDTO> getAll() {
        return ReservationMapper.toResponseList(reservationRepository.findAll());
    }

    public List<ReservationResponseDTO> getByDateRange(LocalDate start, LocalDate end) {
        validateDates(start, end);
        return ReservationMapper.toResponseList(
                reservationRepository.findWithinDateRange(start, end)
        );
    }

    public void delete(Integer reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        reservationRepository.delete(res);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("checkInDate must be before checkOutDate");
        }
    }
}