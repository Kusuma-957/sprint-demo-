package com.hotelManagement.system;

import com.hotelManagement.system.dto.HotelAmenityCreateDTO;
import com.hotelManagement.system.dto.HotelAmenityResponseDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelAmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import com.hotelManagement.system.service.HotelAmenityService;

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
class HotelAmenityServiceTest {

    @Mock private HotelAmenityRepository hotelAmenityRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private AmenityRepository amenityRepository;

    @InjectMocks
    private HotelAmenityService service;

    private HotelAmenityCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new HotelAmenityCreateDTO();
        createDto.setHotelId(1001);
        createDto.setAmenityId(2002);
    }

    private static Hotel hotel(Integer id) {
        Hotel h = new Hotel();
        // Assumes your Hotel entity has setHotelId(Integer); adjust if different
        h.setHotelId(id);
        return h;
    }

    private static Amenity amenity(Integer id) {
        Amenity a = new Amenity();
        a.setAmenityId(id);
        return a;
    }

    // ---------- Negative paths ----------

    @Test
    @DisplayName("createMapping: when hotel not found -> throws ResourceNotFoundException")
    void createMapping_hotelMissing_throws() {
        when(hotelRepository.findById(1001)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createMapping(createDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hotel not found: 1001");

        verify(hotelRepository).findById(1001);
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(amenityRepository, hotelAmenityRepository);
    }

    @Test
    @DisplayName("createMapping: when amenity not found -> throws ResourceNotFoundException")
    void createMapping_amenityMissing_throws() {
        when(hotelRepository.findById(1001)).thenReturn(Optional.of(hotel(1001)));
        when(amenityRepository.findById(2002)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createMapping(createDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amenity not found: 2002");

        verify(hotelRepository).findById(1001);
        verify(amenityRepository).findById(2002);
        verifyNoMoreInteractions(hotelRepository, amenityRepository);
        verifyNoInteractions(hotelAmenityRepository);
    }

    @Test
    @DisplayName("createMapping: when mapping exists -> throws ConflictException")
    void createMapping_duplicate_throwsConflict() {
        when(hotelRepository.findById(1001)).thenReturn(Optional.of(hotel(1001)));
        when(amenityRepository.findById(2002)).thenReturn(Optional.of(amenity(2002)));
        when(hotelAmenityRepository.existsMapping(1001, 2002)).thenReturn(true);

        assertThatThrownBy(() -> service.createMapping(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Hotel amenity already exist");

        verify(hotelRepository).findById(1001);
        verify(amenityRepository).findById(2002);
        verify(hotelAmenityRepository).existsMapping(1001, 2002);
        verifyNoMoreInteractions(hotelAmenityRepository);
    }

    // ---------- Happy path ----------

    @Test
    @DisplayName("createMapping: happy path -> inserts mapping and returns response DTO")
    void createMapping_ok_insertsAndReturns() {
        when(hotelRepository.findById(1001)).thenReturn(Optional.of(hotel(1001)));
        when(amenityRepository.findById(2002)).thenReturn(Optional.of(amenity(2002)));
        when(hotelAmenityRepository.existsMapping(1001, 2002)).thenReturn(false);

        HotelAmenityResponseDTO resp = service.createMapping(createDto);

        assertThat(resp).isNotNull();
        assertThat(resp.getHotelId()).isEqualTo(1001);
        assertThat(resp.getAmenityId()).isEqualTo(2002);

        verify(hotelRepository).findById(1001);
        verify(amenityRepository).findById(2002);
        verify(hotelAmenityRepository).existsMapping(1001, 2002);
        verify(hotelAmenityRepository).insertMapping(1001, 2002);
        verifyNoMoreInteractions(hotelAmenityRepository);
    }
}