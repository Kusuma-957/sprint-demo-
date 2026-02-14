//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.ReservationCreateDTO;
//import com.hotelManagement.system.dto.ReservationResponseDTO;
//import com.hotelManagement.system.dto.ReservationUpdateDTO;
//import com.hotelManagement.system.entity.Reservation;
//import com.hotelManagement.system.entity.Room;
//import com.hotelManagement.system.mapper.ReservationMapper;
//import com.hotelManagement.system.repository.ReservationRepository;
//import com.hotelManagement.system.repository.RoomRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ReservationService {
//
//    private final ReservationRepository reservationRepository;
//    private final RoomRepository roomRepository;
//
//    public ReservationResponseDTO create(ReservationCreateDTO dto) {
//        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());
//
//        Room room = roomRepository.findById(dto.getRoomId())
//                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.getRoomId()));
//
//        // no overlap per room
//        boolean overlap = reservationRepository.existsOverlap(
//                room.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate());
//        if (overlap) {
//            throw new IllegalStateException("Room is not available for the selected date range");
//        }
//
//        Reservation res = ReservationMapper.toEntity(dto);
//        res.setRoom(room);
//
//        return ReservationMapper.toResponse(reservationRepository.save(res));
//    }
//
//    public ReservationResponseDTO update(Integer reservationId, ReservationUpdateDTO dto) {
//        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());
//
//        Reservation res = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
//
//        // If you allow moving rooms on update, fetch room by dto.getRoomId() and set here.
//        // For now, use existing res.getRoom().
//        boolean overlap = reservationRepository.existsOverlapExcluding(
//                res.getReservationId(),
//                res.getRoom().getRoomId(),
//                dto.getCheckInDate(),
//                dto.getCheckOutDate()
//        );
//        if (overlap) {
//            throw new IllegalStateException("Room is not available for the selected date range");
//        }
//
//        ReservationMapper.updateEntity(dto, res);
//        return ReservationMapper.toResponse(reservationRepository.save(res));
//    }
//
//    public ReservationResponseDTO get(Integer reservationId) {
//        Reservation res = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
//        return ReservationMapper.toResponse(res);
//    }
//
//    public List<ReservationResponseDTO> getAll() {
//        return ReservationMapper.toResponseList(reservationRepository.findAll());
//    }
//
//    public List<ReservationResponseDTO> getByDateRange(LocalDate start, LocalDate end) {
//        validateDates(start, end);
//        return ReservationMapper.toResponseList(
//                reservationRepository.findWithinDateRange(start, end)
//        );
//    }
//
//    public void delete(Integer reservationId) {
//        Reservation res = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
//        reservationRepository.delete(res);
//    }
//
//    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
//        if (checkIn == null || checkOut == null) {
//            throw new IllegalArgumentException("Dates cannot be null");
//        }
//        if (!checkIn.isBefore(checkOut)) {
//            throw new IllegalArgumentException("checkInDate must be before checkOutDate");
//        }
//    }
//}


package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.ReservationCreateDTO;
import com.hotelManagement.system.dto.ReservationResponseDTO;
import com.hotelManagement.system.dto.ReservationUpdateDTO;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.exception.BadRequestException;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
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

	public void create(ReservationCreateDTO dto) {

		// 1) Basic date validation (precondition)
		if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
			throw new BadRequestException("check-in and check-out dates are required");
		}
		if (!dto.getCheckInDate().isBefore(dto.getCheckOutDate())) {
			throw new BadRequestException("check-in date must be before check-out date");
		}
		// Optional: Disallow past check-in
		// if (dto.getCheckInDate().isBefore(LocalDate.now())) {
		//     throw new BadRequestException("check-in date cannot be in the past");
		// }

		// 2) Resolve the room
		Room room = roomRepository.findById(dto.getRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Room doesn't exist"));

		// 3) Check room-level availability flag (maintenance/blocked/offline)
		//    If your Room entity uses isAvailable: Boolean getIsAvailable()
		if (Boolean.FALSE.equals(room.getIsAvailable())) {
			// Still a conflict scenario for POST → map to ADDFAILS via GlobalExceptionHandler
			throw new ConflictException("Room is not available");
		}

		// 4) Check double-booking for the date range
		boolean overlaps = reservationRepository.existsOverlap(
				dto.getRoomId(),
				dto.getCheckInDate(),
				dto.getCheckOutDate()
				);
		if (overlaps) {
			// Reservation exists for the requested period for this room
			throw new ConflictException("reservation already exist");
		}

		// 5) Persist
		Reservation reservation = ReservationMapper.toEntity(dto);
		reservation.setRoom(room);
		reservationRepository.save(reservation);

		// 6) Flip room availability to false (0) after reserving
		room.setIsAvailable(false);
		roomRepository.save(room);
		reservationRepository.save(reservation);

		// Note: We do NOT flip room.isAvailable to false here because availability is date-based.
		// Keep the flag for permanent/operational availability (e.g., maintenance).
	}

	// GET all
	public List<ReservationResponseDTO> getAll() {
		List<Reservation> list = reservationRepository.findAll();

		if (list.isEmpty()) {
			throw new EmptyListException("reservation list is empty");
		}

		return ReservationMapper.toResponseList(list);
	}

	// GET by ID
	public ReservationResponseDTO get(Integer id) {
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation doesn't exist"));

		return ReservationMapper.toResponse(reservation);
	}

	// GET by date range
	public List<ReservationResponseDTO> getByDateRange(LocalDate start, LocalDate end) {
		List<Reservation> list = reservationRepository.findWithinDateRange(start, end);

		if (list.isEmpty()) {
			throw new EmptyListException("reservation list is empty");
		}

		return ReservationMapper.toResponseList(list);
	}


	public void update(Integer reservationId, ReservationUpdateDTO dto) {
		if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
			throw new BadRequestException("check-in and check-out dates are required");
		}
		if (!dto.getCheckInDate().isBefore(dto.getCheckOutDate())) {
			throw new BadRequestException("check-in date must be before check-out date");
		}

		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation doesn't exist"));

		// If you allow moving rooms on update, fetch room by dto.getRoomId() and set here.
		// For now, use existing res.getRoom().
		Integer roomId = reservation.getRoom().getRoomId();
      if (reservationRepository.existsExactDuplicate(
	                roomId,
	                dto.getCheckInDate(),
	                dto.getCheckOutDate()
	        )) {
	            throw new ConflictException("reservation already exist");
	        }
	
	        ReservationMapper.updateEntity(dto, reservation);
	        reservationRepository.save(reservation);
	}


	// DELETE
	public void delete(Integer reservationId) {

		if (!reservationRepository.existsById(reservationId)) {
			throw new ResourceNotFoundException("Reservation doesn't exist");
		}

		reservationRepository.deleteById(reservationId);
	}

}