package com.hotelManagement.system;

import com.hotelManagement.system.dto.RoomTypeCreateDTO;
import com.hotelManagement.system.dto.RoomTypeResponseDTO;
import com.hotelManagement.system.dto.RoomTypeUpdateDTO;
import com.hotelManagement.system.entity.RoomType;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.RoomTypeRepository;
import com.hotelManagement.system.service.RoomTypeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private RoomTypeService roomTypeService;

    private RoomTypeCreateDTO createDto;
    private RoomTypeUpdateDTO updateDto;

    @BeforeEach
    void setUp() {
        createDto = new RoomTypeCreateDTO();
        createDto.setTypeName("Deluxe");
        createDto.setMaxOccupancy(3);
        createDto.setPricePerNight(new BigDecimal("4999.99"));
        createDto.setDescription("Nice large room");

        updateDto = new RoomTypeUpdateDTO();
        updateDto.setTypeName("Super Deluxe");
        updateDto.setMaxOccupancy(4);
        updateDto.setPricePerNight(new BigDecimal("5999.50"));
        updateDto.setDescription("Upgraded room");
    }

    private static RoomType type(Integer id, String name, int occupancy, String price, String desc) {
        RoomType t = new RoomType();
        t.setRoomTypeId(id);
        t.setTypeName(name);
        t.setMaxOccupancy(occupancy);
        t.setPricePerNight(new BigDecimal(price));
        t.setDescription(desc);
        return t;
    }

    // -------------------- create --------------------

    @Test
    @DisplayName("create: when typeName exists (ignore case) -> throws ConflictException")
    void create_duplicate_throwsConflict() {
        when(roomTypeRepository.existsByTypeNameIgnoreCase("Deluxe")).thenReturn(true);

        assertThatThrownBy(() -> roomTypeService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("RoomType already exist");

        verify(roomTypeRepository).existsByTypeNameIgnoreCase("Deluxe");
        verifyNoMoreInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("create: unique -> saves and returns mapped response")
    void create_unique_saves() {
        when(roomTypeRepository.existsByTypeNameIgnoreCase("Deluxe")).thenReturn(false);

        RoomType saved = type(101, "Deluxe", 3, "4999.99", "Nice large room");
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(saved);

        RoomTypeResponseDTO resp = roomTypeService.create(createDto);

        assertThat(resp).isNotNull();
        assertThat(resp.getRoomTypeId()).isEqualTo(101);
        assertThat(resp.getTypeName()).isEqualTo("Deluxe");
        assertThat(resp.getMaxOccupancy()).isEqualTo(3);
        assertThat(resp.getPricePerNight()).isEqualByComparingTo("4999.99");
        assertThat(resp.getDescription()).isEqualTo("Nice large room");

        verify(roomTypeRepository).existsByTypeNameIgnoreCase("Deluxe");
        verify(roomTypeRepository).save(any(RoomType.class));
        verifyNoMoreInteractions(roomTypeRepository);
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("update: when id not found -> throws ResourceNotFoundException")
    void update_notFound_throws() {
        when(roomTypeRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomTypeService.update(999, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("RoomType doesn't exist");

        verify(roomTypeRepository).findById(999);
        verifyNoMoreInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("update: found -> saves and returns updated response")
    void update_ok_saves() {
        RoomType existing = type(7, "Old", 2, "3999.00", "old");
        when(roomTypeRepository.findById(7)).thenReturn(Optional.of(existing));

        RoomType saved = type(7, "Super Deluxe", 4, "5999.50", "Upgraded room");
        when(roomTypeRepository.save(existing)).thenReturn(saved);

        RoomTypeResponseDTO resp = roomTypeService.update(7, updateDto);

        assertThat(resp.getRoomTypeId()).isEqualTo(7);
        assertThat(resp.getTypeName()).isEqualTo("Super Deluxe");
        assertThat(resp.getMaxOccupancy()).isEqualTo(4);
        assertThat(resp.getPricePerNight()).isEqualByComparingTo("5999.50");
        assertThat(resp.getDescription()).isEqualTo("Upgraded room");

        verify(roomTypeRepository).findById(7);
        verify(roomTypeRepository).save(existing);
        verifyNoMoreInteractions(roomTypeRepository);
    }

    // -------------------- get --------------------

    @Test
    @DisplayName("get: when id not found -> throws ResourceNotFoundException")
    void get_notFound_throws() {
        when(roomTypeRepository.findById(12)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomTypeService.get(12))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("RoomType doesn't exist");

        verify(roomTypeRepository).findById(12);
        verifyNoMoreInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("get: found -> returns mapped response")
    void get_found_returns() {
        RoomType found = type(5, "Suite", 5, "9999.00", "Big suite");
        when(roomTypeRepository.findById(5)).thenReturn(Optional.of(found));

        RoomTypeResponseDTO resp = roomTypeService.get(5);

        assertThat(resp.getRoomTypeId()).isEqualTo(5);
        assertThat(resp.getTypeName()).isEqualTo("Suite");
        assertThat(resp.getMaxOccupancy()).isEqualTo(5);
        assertThat(resp.getPricePerNight()).isEqualByComparingTo("9999.00");
        assertThat(resp.getDescription()).isEqualTo("Big suite");

        verify(roomTypeRepository).findById(5);
        verifyNoMoreInteractions(roomTypeRepository);
    }

    // -------------------- getAll --------------------

    @Test
    @DisplayName("getAll: empty -> returns empty list (no exception)")
    void getAll_empty_returnsEmpty() {
        when(roomTypeRepository.findAll()).thenReturn(List.of());

        List<RoomTypeResponseDTO> list = roomTypeService.getAll();

        assertThat(list).isEmpty();

        verify(roomTypeRepository).findAll();
        verifyNoMoreInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("getAll: non-empty -> returns mapped list")
    void getAll_nonEmpty_returnsList() {
        when(roomTypeRepository.findAll()).thenReturn(List.of(
                type(1, "Deluxe", 3, "4999.99", "desc1"),
                type(2, "Suite", 5, "9999.00", "desc2")
        ));

        List<RoomTypeResponseDTO> list = roomTypeService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRoomTypeId()).isEqualTo(1);
        assertThat(list.get(0).getTypeName()).isEqualTo("Deluxe");
        assertThat(list.get(0).getPricePerNight()).isEqualByComparingTo("4999.99");

        assertThat(list.get(1).getRoomTypeId()).isEqualTo(2);
        assertThat(list.get(1).getTypeName()).isEqualTo("Suite");
        assertThat(list.get(1).getPricePerNight()).isEqualByComparingTo("9999.00");

        verify(roomTypeRepository).findAll();
        verifyNoMoreInteractions(roomTypeRepository);
    }

    // -------------------- delete --------------------

    @Test
    @DisplayName("delete: missing -> throws ResourceNotFoundException")
    void delete_missing_throws() {
        when(roomTypeRepository.existsById(77)).thenReturn(false);

        assertThatThrownBy(() -> roomTypeService.delete(77))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("RoomType doesn't exist");

        verify(roomTypeRepository).existsById(77);
        verifyNoMoreInteractions(roomTypeRepository);
    }

    @Test
    @DisplayName("delete: exists -> calls deleteById")
    void delete_ok() {
        when(roomTypeRepository.existsById(78)).thenReturn(true);

        roomTypeService.delete(78);

        verify(roomTypeRepository).existsById(78);
        verify(roomTypeRepository).deleteById(78);
        verifyNoMoreInteractions(roomTypeRepository);
    }
}