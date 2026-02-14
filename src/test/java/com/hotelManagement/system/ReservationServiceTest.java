package com.hotelManagement.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import com.hotelManagement.system.dto.ReservationCreateDTO;
import com.hotelManagement.system.dto.ReservationResponseDTO;
import com.hotelManagement.system.dto.ReservationUpdateDTO;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.exception.BadRequestException;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.ReservationRepository;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.service.ReservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationCreateDTO createDto;
    private ReservationUpdateDTO updateDto;

    private final LocalDate IN  = LocalDate.of(2026, 2, 20);
    private final LocalDate OUT = LocalDate.of(2026, 2, 25);

    @BeforeEach
    void setUp() {
        createDto = new ReservationCreateDTO();
        createDto.setRoomId(10);
        createDto.setGuestName("Alice");
        createDto.setGuestEmail("alice@example.com");
        createDto.setGuestPhone("9999999999");
        createDto.setCheckInDate(IN);
        createDto.setCheckOutDate(OUT);

        updateDto = new ReservationUpdateDTO();
        updateDto.setGuestName("Alice Updated");
        updateDto.setGuestEmail("alice.updated@example.com");
        updateDto.setGuestPhone("8888888888");
        updateDto.setCheckInDate(LocalDate.of(2026, 3, 1));
        updateDto.setCheckOutDate(LocalDate.of(2026, 3, 3));
    }

    private static Room room(Integer id, boolean available) {
        Room r = new Room();
        r.setRoomId(id);
        r.setIsAvailable(available);
        return r;
    }

    private static Reservation res(Integer id, Room room, LocalDate in, LocalDate out) {
        Reservation r = new Reservation();
        r.setReservationId(id);
        r.setRoom(room);
        r.setGuestName("G");
        r.setGuestEmail("g@mail.com");
        r.setGuestPhone("123");
        r.setCheckInDate(in);
        r.setCheckOutDate(out);
        return r;
    }

    // -------------------- create --------------------

    @Test
    @DisplayName("create: null dates -> BadRequestException")
    void create_nullDates_badRequest() {
        createDto.setCheckInDate(null);
        createDto.setCheckOutDate(null);

        assertThatThrownBy(() -> reservationService.create(createDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("check-in and check-out dates are required");

        verifyNoInteractions(roomRepository, reservationRepository);
    }

    @Test
    @DisplayName("create: check-in NOT before check-out -> BadRequestException")
    void create_invalidDateOrder_badRequest() {
        createDto.setCheckInDate(LocalDate.of(2026, 2, 25));
        createDto.setCheckOutDate(LocalDate.of(2026, 2, 25)); // equal

        assertThatThrownBy(() -> reservationService.create(createDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("check-in date must be before check-out date");

        verifyNoInteractions(roomRepository, reservationRepository);
    }

    @Test
    @DisplayName("create: room not found -> ResourceNotFoundException")
    void create_roomMissing_notFound() {
        when(roomRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room doesn't exist");

        verify(roomRepository).findById(10);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("create: room not available -> ConflictException")
    void create_roomNotAvailable_conflict() {
        when(roomRepository.findById(10)).thenReturn(Optional.of(room(10, false)));

        assertThatThrownBy(() -> reservationService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room is not available");

        verify(roomRepository).findById(10);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("create: overlap exists -> ConflictException")
    void create_overlap_conflict() {
        when(roomRepository.findById(10)).thenReturn(Optional.of(room(10, true)));
        when(reservationRepository.existsOverlap(10, IN, OUT)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("reservation already exist");

        verify(roomRepository).findById(10);
        verify(reservationRepository).existsOverlap(10, IN, OUT);
        verifyNoMoreInteractions(roomRepository, reservationRepository);
    }

    @Test
    @DisplayName("create: happy path -> saves reservation, flips room availability, saves again")
    void create_ok_savesAndFlipsAvailability() {
        Room foundRoom = room(10, true);
        when(roomRepository.findById(10)).thenReturn(Optional.of(foundRoom));
        when(reservationRepository.existsOverlap(10, IN, OUT)).thenReturn(false);

        // First save: assign ID to reservation
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            if (r.getReservationId() == null) r.setReservationId(999);
            return r;
        });

        reservationService.create(createDto);

        // Verify order: find room -> check overlap -> save(res) -> set room false -> save(room) -> save(res)
        InOrder inOrder = inOrder(roomRepository, reservationRepository);
        inOrder.verify(roomRepository).findById(10);
        inOrder.verify(reservationRepository).existsOverlap(10, IN, OUT);
        inOrder.verify(reservationRepository).save(any(Reservation.class));
        inOrder.verify(roomRepository).save(foundRoom);
        inOrder.verify(reservationRepository).save(any(Reservation.class));

        assertThat(foundRoom.getIsAvailable()).isFalse();
    }

    // -------------------- getAll --------------------

    @Test
    @DisplayName("getAll: empty -> EmptyListException")
    void getAll_empty_throws() {
        when(reservationRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> reservationService.getAll())
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("reservation list is empty");

        verify(reservationRepository).findAll();
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("getAll: non-empty -> mapped list")
    void getAll_nonEmpty_returnsList() {
        Room r1 = room(101, true);
        Room r2 = room(102, true);

        when(reservationRepository.findAll()).thenReturn(List.of(
                res(1, r1, LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12)),
                res(2, r2, LocalDate.of(2026, 2, 15), LocalDate.of(2026, 2, 18))
        ));

        List<ReservationResponseDTO> list = reservationService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getReservationId()).isEqualTo(1);
        assertThat(list.get(0).getRoomId()).isEqualTo(101);
        assertThat(list.get(1).getReservationId()).isEqualTo(2);
        assertThat(list.get(1).getRoomId()).isEqualTo(102);

        verify(reservationRepository).findAll();
        verifyNoMoreInteractions(reservationRepository);
    }

    // -------------------- get --------------------

    @Test
    @DisplayName("get: missing -> ResourceNotFoundException")
    void get_missing_notFound() {
        when(reservationRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.get(5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation doesn't exist");

        verify(reservationRepository).findById(5);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("get: found -> mapped DTO")
    void get_found_returnsDto() {
        Room rm = room(303, true);
        Reservation reservation = res(7, rm, LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 23));
        when(reservationRepository.findById(7)).thenReturn(Optional.of(reservation));

        ReservationResponseDTO dto = reservationService.get(7);

        assertThat(dto.getReservationId()).isEqualTo(7);
        assertThat(dto.getRoomId()).isEqualTo(303);
        assertThat(dto.getCheckInDate()).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(dto.getCheckOutDate()).isEqualTo(LocalDate.of(2026, 2, 23));

        verify(reservationRepository).findById(7);
        verifyNoMoreInteractions(reservationRepository);
    }

    // -------------------- getByDateRange --------------------

    @Test
    @DisplayName("getByDateRange: empty -> EmptyListException")
    void getByDateRange_empty_throws() {
        LocalDate s = LocalDate.of(2026, 2, 1);
        LocalDate e = LocalDate.of(2026, 2, 28);
        when(reservationRepository.findWithinDateRange(s, e)).thenReturn(List.of());

        assertThatThrownBy(() -> reservationService.getByDateRange(s, e))
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("reservation list is empty");

        verify(reservationRepository).findWithinDateRange(s, e);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("getByDateRange: non-empty -> mapped list")
    void getByDateRange_ok_returnsList() {
        LocalDate s = LocalDate.of(2026, 2, 1);
        LocalDate e = LocalDate.of(2026, 2, 28);
        Room r = room(404, true);

        when(reservationRepository.findWithinDateRange(s, e)).thenReturn(List.of(
                res(21, r, LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12))
        ));

        List<ReservationResponseDTO> list = reservationService.getByDateRange(s, e);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getReservationId()).isEqualTo(21);
        assertThat(list.get(0).getRoomId()).isEqualTo(404);

        verify(reservationRepository).findWithinDateRange(s, e);
        verifyNoMoreInteractions(reservationRepository);
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("update: null dates -> BadRequestException")
    void update_nullDates_badRequest() {
        updateDto.setCheckInDate(null);
        updateDto.setCheckOutDate(null);

        assertThatThrownBy(() -> reservationService.update(1, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("check-in and check-out dates are required");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("update: check-in NOT before check-out -> BadRequestException")
    void update_invalidDateOrder_badRequest() {
        updateDto.setCheckInDate(LocalDate.of(2026, 3, 5));
        updateDto.setCheckOutDate(LocalDate.of(2026, 3, 5)); // equal

        assertThatThrownBy(() -> reservationService.update(1, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("check-in date must be before check-out date");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("update: reservation not found -> ResourceNotFoundException")
    void update_resMissing_notFound() {
        when(reservationRepository.findById(55)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.update(55, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation doesn't exist");

        verify(reservationRepository).findById(55);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("update: exact duplicate exists for same room & dates -> ConflictException")
    void update_duplicate_conflict() {
        Room rm = room(606, true);
        Reservation existing = res(66, rm, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 3));
        when(reservationRepository.findById(66)).thenReturn(Optional.of(existing));

        when(reservationRepository.existsExactDuplicate(606, updateDto.getCheckInDate(), updateDto.getCheckOutDate()))
                .thenReturn(true);

        assertThatThrownBy(() -> reservationService.update(66, updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("reservation already exist");

        verify(reservationRepository).findById(66);
        verify(reservationRepository).existsExactDuplicate(606, updateDto.getCheckInDate(), updateDto.getCheckOutDate());
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("update: happy path -> mapper updates and save is called")
    void update_ok_saves() {
        Room rm = room(707, true);
        Reservation existing = res(77, rm, LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12));
        when(reservationRepository.findById(77)).thenReturn(Optional.of(existing));
        when(reservationRepository.existsExactDuplicate(707, updateDto.getCheckInDate(), updateDto.getCheckOutDate()))
                .thenReturn(false);

        when(reservationRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0));

        reservationService.update(77, updateDto);

        assertThat(existing.getGuestName()).isEqualTo("Alice Updated");
        assertThat(existing.getGuestEmail()).isEqualTo("alice.updated@example.com");
        assertThat(existing.getGuestPhone()).isEqualTo("8888888888");
        assertThat(existing.getCheckInDate()).isEqualTo(updateDto.getCheckInDate());
        assertThat(existing.getCheckOutDate()).isEqualTo(updateDto.getCheckOutDate());

        verify(reservationRepository).findById(77);
        verify(reservationRepository).existsExactDuplicate(707, updateDto.getCheckInDate(), updateDto.getCheckOutDate());
        verify(reservationRepository).save(existing);
        verifyNoMoreInteractions(reservationRepository);
    }

    // -------------------- delete --------------------

    @Test
    @DisplayName("delete: missing -> ResourceNotFoundException")
    void delete_missing_notFound() {
        when(reservationRepository.existsById(91)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.delete(91))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation doesn't exist");

        verify(reservationRepository).existsById(91);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    @DisplayName("delete: exists -> deleteById called")
    void delete_ok() {
        when(reservationRepository.existsById(92)).thenReturn(true);

        reservationService.delete(92);

        verify(reservationRepository).existsById(92);
        verify(reservationRepository).deleteById(92);
        verifyNoMoreInteractions(reservationRepository);
    }
}

