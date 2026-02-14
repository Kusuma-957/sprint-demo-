package com.hotelManagement.system;

import com.hotelManagement.system.dto.AmenityCreateDTO;
import com.hotelManagement.system.dto.AmenityResponseDTO;
import com.hotelManagement.system.dto.AmenityUpdateDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.service.AmenityService;

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
class AmenityServiceTest {

    @Mock private AmenityRepository amenityRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private HotelRepository hotelRepository;

    @InjectMocks
    private AmenityService amenityService;

    private AmenityCreateDTO createDto;
    private AmenityUpdateDTO updateDto;

    @BeforeEach
    void setUp() {
        createDto = new AmenityCreateDTO();
        createDto.setName("WiFi");
        createDto.setDescription("High-speed internet access");

        updateDto = new AmenityUpdateDTO();
        updateDto.setName("Premium WiFi");
        updateDto.setDescription("Upgraded high-speed internet");
    }

    private static Amenity amenity(Integer id, String name, String desc) {
        Amenity a = new Amenity();
        a.setAmenityId(id);
        a.setName(name);
        a.setDescription(desc);
        return a;
    }

    // -------------------- create --------------------

    @Test
    @DisplayName("create: unique name → saves and returns mapped DTO")
    void create_unique_saves() {
        when(amenityRepository.existsByNameIgnoreCase("WiFi")).thenReturn(false);

        Amenity saved = amenity(101, "WiFi", "High-speed internet access");
        when(amenityRepository.save(any(Amenity.class))).thenReturn(saved);

        AmenityResponseDTO resp = amenityService.create(createDto);

        assertThat(resp).isNotNull();
        assertThat(resp.getAmenityId()).isEqualTo(101);
        assertThat(resp.getName()).isEqualTo("WiFi");
        assertThat(resp.getDescription()).isEqualTo("High-speed internet access");

        verify(amenityRepository).existsByNameIgnoreCase("WiFi");
        verify(amenityRepository).save(any(Amenity.class));
        verifyNoMoreInteractions(amenityRepository);
        verifyNoInteractions(roomRepository, hotelRepository);
    }

    @Test
    @DisplayName("create: duplicate name (case-insensitive) → throws ConflictException")
    void create_duplicate_throwsConflict() {
        when(amenityRepository.existsByNameIgnoreCase("WiFi")).thenReturn(true);

        assertThatThrownBy(() -> amenityService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Amenity already exists");

        verify(amenityRepository).existsByNameIgnoreCase("WiFi");
        verifyNoMoreInteractions(amenityRepository);
        verifyNoInteractions(roomRepository, hotelRepository);
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("update: not found → throws ResourceNotFoundException")
    void update_notFound_throws() {
        when(amenityRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.update(999, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amenity doesn't exist");

        verify(amenityRepository).findById(999);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("update: different name & duplicate exists → throws ConflictException")
    void update_duplicateName_throws() {
        Amenity existing = amenity(10, "WiFi", "Old description");
        when(amenityRepository.findById(10)).thenReturn(Optional.of(existing));
        when(amenityRepository.existsByNameIgnoreCase("Premium WiFi")).thenReturn(true);

        assertThatThrownBy(() -> amenityService.update(10, updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Another amenity with same name");

        verify(amenityRepository).findById(10);
        verify(amenityRepository).existsByNameIgnoreCase("Premium WiFi");
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("update: same name (case-insensitive) → allowed; saves with new description")
    void update_sameName_ok() {
        // Make DTO name same as existing name ignoring case
        Amenity existing = amenity(11, "WiFi", "Old desc");
        AmenityUpdateDTO dto = new AmenityUpdateDTO();
        dto.setName("wifi"); // same ignoring case
        dto.setDescription("New desc");

        when(amenityRepository.findById(11)).thenReturn(Optional.of(existing));
        // existsByNameIgnoreCase SHOULD NOT be called because name didn't change logically
        Amenity saved = amenity(11, "wifi", "New desc");
        when(amenityRepository.save(existing)).thenReturn(saved);

        AmenityResponseDTO resp = amenityService.update(11, dto);

        assertThat(resp.getAmenityId()).isEqualTo(11);
        assertThat(resp.getName()).isEqualTo("wifi");
        assertThat(resp.getDescription()).isEqualTo("New desc");

        verify(amenityRepository).findById(11);
        verify(amenityRepository).save(existing);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("update: happy path → saves and returns")
    void update_ok() {
        Amenity existing = amenity(12, "Wifi Basic", "basic");
        when(amenityRepository.findById(12)).thenReturn(Optional.of(existing));
        when(amenityRepository.existsByNameIgnoreCase("Premium WiFi")).thenReturn(false);

        Amenity saved = amenity(12, "Premium WiFi", "Upgraded high-speed internet");
        when(amenityRepository.save(existing)).thenReturn(saved);

        AmenityResponseDTO resp = amenityService.update(12, updateDto);

        assertThat(resp.getAmenityId()).isEqualTo(12);
        assertThat(resp.getName()).isEqualTo("Premium WiFi");
        assertThat(resp.getDescription()).isEqualTo("Upgraded high-speed internet");

        verify(amenityRepository).findById(12);
        verify(amenityRepository).existsByNameIgnoreCase("Premium WiFi");
        verify(amenityRepository).save(existing);
        verifyNoMoreInteractions(amenityRepository);
    }

    // -------------------- get --------------------

    @Test
    @DisplayName("get: found → returns DTO")
    void get_found_ok() {
        Amenity found = amenity(21, "Pool", "Indoor pool");
        when(amenityRepository.findById(21)).thenReturn(Optional.of(found));

        AmenityResponseDTO resp = amenityService.get(21);

        assertThat(resp.getAmenityId()).isEqualTo(21);
        assertThat(resp.getName()).isEqualTo("Pool");
        assertThat(resp.getDescription()).isEqualTo("Indoor pool");

        verify(amenityRepository).findById(21);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("get: missing → throws ResourceNotFoundException")
    void get_missing_throws() {
        when(amenityRepository.findById(22)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.get(22))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amenity doesn't exist");

        verify(amenityRepository).findById(22);
        verifyNoMoreInteractions(amenityRepository);
    }

    // -------------------- getAll --------------------

    @Test
    @DisplayName("getAll: returns mapped list (non-empty)")
    void getAll_nonEmpty_returnsList() {
        when(amenityRepository.findAll()).thenReturn(List.of(
                amenity(1, "WiFi", "desc1"),
                amenity(2, "Pool", "desc2")
        ));

        List<AmenityResponseDTO> list = amenityService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getAmenityId()).isEqualTo(1);
        assertThat(list.get(1).getName()).isEqualTo("Pool");

        verify(amenityRepository).findAll();
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("getAll: empty list → returns empty (no exception in current implementation)")
    void getAll_empty_returnsEmptyList() {
        when(amenityRepository.findAll()).thenReturn(List.of());

        List<AmenityResponseDTO> list = amenityService.getAll();

        assertThat(list).isEmpty();

        verify(amenityRepository).findAll();
        verifyNoMoreInteractions(amenityRepository);
    }

    // -------------------- getByRoomId --------------------

    @Test
    @DisplayName("getByRoomId: room missing → throws ResourceNotFoundException")
    void getByRoomId_roomMissing_throws() {
        when(roomRepository.existsById(77)).thenReturn(false);

        assertThatThrownBy(() -> amenityService.getByRoomId(77))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found with the given roomId");

        verify(roomRepository).existsById(77);
        verifyNoMoreInteractions(roomRepository);
        verifyNoInteractions(amenityRepository);
    }

    @Test
    @DisplayName("getByRoomId: room exists → returns mapped amenities")
    void getByRoomId_ok_returnsList() {
        when(roomRepository.existsById(78)).thenReturn(true);

        when(amenityRepository.findByRoomId(78)).thenReturn(List.of(
                amenity(31, "WiFi", "d1"),
                amenity(32, "AC", "d2")
        ));

        List<AmenityResponseDTO> list = amenityService.getByRoomId(78);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getAmenityId()).isEqualTo(31);
        assertThat(list.get(1).getName()).isEqualTo("AC");

        verify(roomRepository).existsById(78);
        verify(amenityRepository).findByRoomId(78);
        verifyNoMoreInteractions(roomRepository, amenityRepository);
    }

    // -------------------- getByHotelId --------------------

    @Test
    @DisplayName("getByHotelId: hotel missing → throws ResourceNotFoundException")
    void getByHotelId_hotelMissing_throws() {
        when(hotelRepository.existsById(88)).thenReturn(false);

        assertThatThrownBy(() -> amenityService.getByHotelId(88))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hotel not found with the given hotelId");

        verify(hotelRepository).existsById(88);
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(amenityRepository);
    }

    @Test
    @DisplayName("getByHotelId: hotel exists → returns mapped amenities")
    void getByHotelId_ok_returnsList() {
        when(hotelRepository.existsById(89)).thenReturn(true);

        when(amenityRepository.findByHotelId(89)).thenReturn(List.of(
                amenity(41, "Spa", "relax"),
                amenity(42, "Gym", "fitness")
        ));

        List<AmenityResponseDTO> list = amenityService.getByHotelId(89);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getAmenityId()).isEqualTo(41);
        assertThat(list.get(1).getName()).isEqualTo("Gym");

        verify(hotelRepository).existsById(89);
        verify(amenityRepository).findByHotelId(89);
        verifyNoMoreInteractions(hotelRepository, amenityRepository);
    }

    // -------------------- delete --------------------

    @Test
    @DisplayName("delete: amenity missing → throws ResourceNotFoundException")
    void delete_missing_throws() {
        when(amenityRepository.existsById(66)).thenReturn(false);

        assertThatThrownBy(() -> amenityService.delete(66))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amenity doesn't exist");

        verify(amenityRepository).existsById(66);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("delete: amenity exists → calls deleteById")
    void delete_ok() {
        when(amenityRepository.existsById(67)).thenReturn(true);

        amenityService.delete(67);

        verify(amenityRepository).existsById(67);
        verify(amenityRepository).deleteById(67);
        verifyNoMoreInteractions(amenityRepository);
    }
}