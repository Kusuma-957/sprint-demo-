package com.hotelManagement.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.hotelManagement.system.dto.RoomAmenityCreateDTO;
import com.hotelManagement.system.dto.RoomAmenityResponseDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.RoomAmenityRepository;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.service.RoomAmenityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAmenityServiceTest {

    @Mock private RoomAmenityRepository roomAmenityRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private AmenityRepository amenityRepository;

    @InjectMocks
    private RoomAmenityService service;

    private RoomAmenityCreateDTO dto;

    @BeforeEach
    void setUp() {
        dto = new RoomAmenityCreateDTO();
        dto.setRoomId(123);
        dto.setAmenityId(456);
    }

    private static Room room(Integer id) {
        Room r = new Room();
        r.setRoomId(id);
        return r;
    }

    private static Amenity amenity(Integer id) {
        Amenity a = new Amenity();
        a.setAmenityId(id);
        return a;
    }

    // -------------------- Negative paths --------------------

    @Test
    @DisplayName("createMapping: when room not found -> throws ResourceNotFoundException")
    void createMapping_roomMissing_throws() {
        when(roomRepository.findById(123)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createMapping(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found: 123");

        verify(roomRepository).findById(123);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(amenityRepository, roomAmenityRepository);
    }

    @Test
    @DisplayName("createMapping: when amenity not found -> throws ResourceNotFoundException")
    void createMapping_amenityMissing_throws() {
        when(roomRepository.findById(123)).thenReturn(Optional.of(room(123)));
        when(amenityRepository.findById(456)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createMapping(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amenity not found: 456");

        verify(roomRepository).findById(123);
        verify(amenityRepository).findById(456);
        verifyNoMoreInteractions(roomRepository, amenityRepository);
        verifyNoInteractions(roomAmenityRepository);
    }

    @Test
    @DisplayName("createMapping: when mapping exists -> throws ConflictException")
    void createMapping_mappingExists_throwsConflict() {
        when(roomRepository.findById(123)).thenReturn(Optional.of(room(123)));
        when(amenityRepository.findById(456)).thenReturn(Optional.of(amenity(456)));
        when(roomAmenityRepository.existsMapping(123, 456)).thenReturn(true);

        assertThatThrownBy(() -> service.createMapping(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room amenity already exist");

        verify(roomRepository).findById(123);
        verify(amenityRepository).findById(456);
        verify(roomAmenityRepository).existsMapping(123, 456);
        verifyNoMoreInteractions(roomAmenityRepository);
    }

    // -------------------- Happy path --------------------

    @Test
    @DisplayName("createMapping: happy path -> inserts mapping and returns response DTO")
    void createMapping_ok_insertsAndReturns() {
        when(roomRepository.findById(123)).thenReturn(Optional.of(room(123)));
        when(amenityRepository.findById(456)).thenReturn(Optional.of(amenity(456)));
        when(roomAmenityRepository.existsMapping(123, 456)).thenReturn(false);

        RoomAmenityResponseDTO resp = service.createMapping(dto);

        assertThat(resp).isNotNull();
        assertThat(resp.getRoomId()).isEqualTo(123);
        assertThat(resp.getAmenityId()).isEqualTo(456);

        verify(roomRepository).findById(123);
        verify(amenityRepository).findById(456);
        verify(roomAmenityRepository).existsMapping(123, 456);
        verify(roomAmenityRepository).insertMapping(123, 456);
        verifyNoMoreInteractions(roomAmenityRepository);
    }
}




