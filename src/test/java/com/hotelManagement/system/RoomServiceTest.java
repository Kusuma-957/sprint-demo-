package com.hotelManagement.system;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.entity.RoomType;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.repository.RoomTypeRepository;
import com.hotelManagement.system.service.RoomService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private RoomService roomService;

    private RoomCreateDTO createDto;
    private RoomUpdateDTO updateDto;

    @BeforeEach
    void setUp() {
        createDto = new RoomCreateDTO();
        createDto.setRoomNumber(101);
        createDto.setRoomTypeId(11);
        createDto.setIsAvailable(true);

        updateDto = new RoomUpdateDTO();
        updateDto.setRoomNumber(202);
        updateDto.setRoomTypeId(22);
        updateDto.setIsAvailable(false);
    }

    private static RoomType type(Integer id) {
        RoomType t = new RoomType();
        t.setRoomTypeId(id);
        return t;
    }

    private static Room room(Integer id, Integer number, Integer typeId, Boolean available) {
        Room r = new Room();
        r.setRoomId(id);
        r.setRoomNumber(number);
        r.setIsAvailable(available);
        r.setRoomType(type(typeId));
        return r;
    }

    // -------------------- create --------------------

    @Test
    @DisplayName("create: when room number already exists -> throws ConflictException")
    void create_duplicateRoomNumber_throws() {
        when(roomRepository.existsByRoomNumber(101)).thenReturn(true);

        assertThatThrownBy(() -> roomService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room already exist");

        verify(roomRepository).existsByRoomNumber(101);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("create: when room type not found -> throws ResourceNotFoundException")
    void create_roomTypeMissing_throws() {
        when(roomRepository.existsByRoomNumber(101)).thenReturn(false);
        when(roomTypeRepository.findById(11)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("RoomType not found");

        verify(roomRepository).existsByRoomNumber(101);
        verify(roomTypeRepository).findById(11);
        verifyNoMoreInteractions(roomRepository, roomTypeRepository);
    }

    @Test
    @DisplayName("create: happy path -> saves room with mapped fields and type")
    void create_ok_saves() {
        when(roomRepository.existsByRoomNumber(101)).thenReturn(false);
        when(roomTypeRepository.findById(11)).thenReturn(Optional.of(type(11)));

        // return any room; we don't assert returned value for create (void method)
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> {
            Room toSave = inv.getArgument(0);
            toSave.setRoomId(555);
            return toSave;
        });

        roomService.create(createDto);

        verify(roomRepository).existsByRoomNumber(101);
        verify(roomTypeRepository).findById(11);
        verify(roomRepository).save(any(Room.class));
        verifyNoMoreInteractions(roomRepository, roomTypeRepository);
    }

    // -------------------- getAll --------------------

    @Test
    @DisplayName("getAll: when empty -> throws EmptyListException")
    void getAll_empty_throws() {
        when(roomRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> roomService.getAll())
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("room list is empty");

        verify(roomRepository).findAll();
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    @DisplayName("getAll: non-empty -> returns mapped DTO list")
    void getAll_nonEmpty_returnsList() {
        when(roomRepository.findAll()).thenReturn(List.of(
                room(1, 101, 11, true),
                room(2, 102, 22, false)
        ));

        List<RoomResponseDTO> list = roomService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRoomId()).isEqualTo(1);
        assertThat(list.get(0).getRoomNumber()).isEqualTo(101);
        assertThat(list.get(0).getRoomTypeId()).isEqualTo(11);
        assertThat(list.get(0).getIsAvailable()).isTrue();

        assertThat(list.get(1).getRoomId()).isEqualTo(2);
        assertThat(list.get(1).getRoomNumber()).isEqualTo(102);
        assertThat(list.get(1).getRoomTypeId()).isEqualTo(22);
        assertThat(list.get(1).getIsAvailable()).isFalse();

        verify(roomRepository).findAll();
        verifyNoMoreInteractions(roomRepository);
    }

    // -------------------- getById --------------------

    @Test
    @DisplayName("getById: when not found -> throws ResourceNotFoundException")
    void getById_missing_throws() {
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("room doesn't exist");

        verify(roomRepository).findById(999);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    @DisplayName("getById: found -> returns mapped DTO")
    void getById_found_returnsDto() {
        Room r = room(10, 301, 33, true);
        when(roomRepository.findById(10)).thenReturn(Optional.of(r));

        RoomResponseDTO dto = roomService.getById(10);

        assertThat(dto.getRoomId()).isEqualTo(10);
        assertThat(dto.getRoomNumber()).isEqualTo(301);
        assertThat(dto.getRoomTypeId()).isEqualTo(33);
        assertThat(dto.getIsAvailable()).isTrue();

        verify(roomRepository).findById(10);
        verifyNoMoreInteractions(roomRepository);
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("update: room not found -> throws ResourceNotFoundException")
    void update_roomMissing_throws() {
        when(roomRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.update(5, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room doesn't exist");

        verify(roomRepository).findById(5);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("update: room type not found -> throws ResourceNotFoundException")
    void update_typeMissing_throws() {
        Room existing = room(6, 111, 9, true);
        when(roomRepository.findById(6)).thenReturn(Optional.of(existing));
        when(roomTypeRepository.findById(22)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.update(6, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("RoomType not found");

        verify(roomRepository).findById(6);
        verify(roomTypeRepository).findById(22);
        verifyNoMoreInteractions(roomRepository, roomTypeRepository);
    }

    @Test
    @DisplayName("update: happy path -> saves updated room")
    void update_ok_saves() {
        Room existing = room(7, 120, 9, true);
        when(roomRepository.findById(7)).thenReturn(Optional.of(existing));
        when(roomTypeRepository.findById(22)).thenReturn(Optional.of(type(22)));

        when(roomRepository.save(existing)).thenAnswer(inv -> {
            Room saved = inv.getArgument(0);
            // after update mapper + setting type and availability/number
            return saved;
        });

        roomService.update(7, updateDto);

        assertThat(existing.getRoomNumber()).isEqualTo(202);
        assertThat(existing.getIsAvailable()).isFalse();
        assertThat(existing.getRoomType()).isNotNull();
        assertThat(existing.getRoomType().getRoomTypeId()).isEqualTo(22);

        verify(roomRepository).findById(7);
        verify(roomTypeRepository).findById(22);
        verify(roomRepository).save(existing);
        verifyNoMoreInteractions(roomRepository, roomTypeRepository);
    }

    // -------------------- getAvailableByType --------------------

    @Test
    @DisplayName("getAvailableByType: empty -> throws EmptyListException")
    void getAvailableByType_empty_throws() {
        when(roomRepository.findAvailableRoomsByType(55)).thenReturn(List.of());

        assertThatThrownBy(() -> roomService.getAvailableByType(55))
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("No room found with given type");

        verify(roomRepository).findAvailableRoomsByType(55);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    @DisplayName("getAvailableByType: non-empty -> returns mapped list")
    void getAvailableByType_ok_returnsList() {
        when(roomRepository.findAvailableRoomsByType(56)).thenReturn(List.of(
                room(41, 401, 77, true),
                room(42, 402, 77, true)
        ));

        List<RoomResponseDTO> list = roomService.getAvailableByType(56);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRoomId()).isEqualTo(41);
        assertThat(list.get(1).getRoomId()).isEqualTo(42);

        verify(roomRepository).findAvailableRoomsByType(56);
        verifyNoMoreInteractions(roomRepository);
    }

    // -------------------- getByAmenity --------------------

    @Test
    @DisplayName("getByAmenity: empty -> throws EmptyListException (amenity doesn't exist with given id)")
    void getByAmenity_empty_throws() {
        when(roomRepository.findRoomsByAmenity(99)).thenReturn(List.of());

        assertThatThrownBy(() -> roomService.getByAmenity(99))
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("amenity doesn't exist with given id");

        verify(roomRepository).findRoomsByAmenity(99);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    @DisplayName("getByAmenity: non-empty -> returns mapped list")
    void getByAmenity_ok_returnsList() {
        when(roomRepository.findRoomsByAmenity(98)).thenReturn(List.of(
                room(51, 501, 66, true),
                room(52, 502, 66, false)
        ));

        List<RoomResponseDTO> list = roomService.getByAmenity(98);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRoomNumber()).isEqualTo(501);
        assertThat(list.get(1).getIsAvailable()).isFalse();

        verify(roomRepository).findRoomsByAmenity(98);
        verifyNoMoreInteractions(roomRepository);
    }

    // -------------------- delete --------------------

    @Test
    @DisplayName("delete: missing -> throws ResourceNotFoundException")
    void delete_missing_throws() {
        when(roomRepository.existsById(73)).thenReturn(false);

        assertThatThrownBy(() -> roomService.delete(73))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room doesn't exist");

        verify(roomRepository).existsById(73);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    @DisplayName("delete: exists -> calls deleteById")
    void delete_ok() {
        when(roomRepository.existsById(74)).thenReturn(true);

        roomService.delete(74);

        verify(roomRepository).existsById(74);
        verify(roomRepository).deleteById(74);
        verifyNoMoreInteractions(roomRepository);
    }
}
