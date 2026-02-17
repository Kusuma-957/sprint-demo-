//package com.hotelManagement.system;
//
//import com.hotelManagement.system.dto.HotelCreateDTO;
//
//import com.hotelManagement.system.dto.HotelResponseDTO;
//import com.hotelManagement.system.dto.HotelUpdateDTO;
//import com.hotelManagement.system.entity.Amenity;
//import com.hotelManagement.system.entity.Hotel;
//import com.hotelManagement.system.exception.ConflictException;
//import com.hotelManagement.system.exception.EmptyListException;
//import com.hotelManagement.system.exception.ResourceNotFoundException;
//import com.hotelManagement.system.repository.AmenityRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//import com.hotelManagement.system.service.HotelService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class HotelServiceTest {
//
//    @Mock private HotelRepository hotelRepository;
//    @Mock private AmenityRepository amenityRepository;
//
//    @InjectMocks
//    private HotelService hotelService;
//    private HotelCreateDTO createDto;
//    private HotelUpdateDTO updateDto;
//
//    @BeforeEach
//    void setUp() {
//        createDto = new HotelCreateDTO();
//        createDto.setName("The Grand");
//        createDto.setLocation("Bangalore");
//        createDto.setDescription("Business hotel");
//
//        updateDto = new HotelUpdateDTO();
//        updateDto.setName("The Grand");
//        updateDto.setLocation("Bangalore");
//        updateDto.setDescription("Updated desc");
//    }
//    private static Hotel hotel(Integer id, String name, String loc, String desc) {
//        Hotel h = new Hotel();
//        h.setHotelId(id);
//        h.setName(name);
//        h.setLocation(loc);
//        h.setDescription(desc);
//        return h;
//    }
//
//    // -------------------- create --------------------
//
//    @Test
//    @DisplayName("create: when unique (no conflict) -> saves and returns mapped DTO")
//    void create_unique_saves() {
//        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
//                .thenReturn(false);
//
//        Hotel toSave = hotel(null, "The Grand", "Bangalore", "Business hotel");
//        Hotel saved  = hotel(101, "The Grand", "Bangalore", "Business hotel");
//
//        // save() will be called with an entity created by HotelMapper; return 'saved'
//        when(hotelRepository.save(any(Hotel.class))).thenReturn(saved);
//
//        HotelResponseDTO resp = hotelService.create(createDto);
//
//        assertThat(resp).isNotNull();
//        assertThat(resp.getId()).isEqualTo(101);
//        assertThat(resp.getName()).isEqualTo("The Grand");
//        //assertEquals("The Grand", resp.getName());
//        assertThat(resp.getLocation()).isEqualTo("Bangalore");
//        assertThat(resp.getDescription()).isEqualTo("Business hotel");
//
//        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
//        verify(hotelRepository).save(any(Hotel.class));
//        verifyNoMoreInteractions(hotelRepository);
//        verifyNoInteractions(amenityRepository);
//    }
//
//    @Test
//    @DisplayName("create: when duplicate (exists) -> throws ConflictException (409)")
//    void create_duplicate_throwsConflict() {
//        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
//                .thenReturn(true);
//
//        assertThatThrownBy(() -> hotelService.create(createDto))
//                .isInstanceOf(ConflictException.class)
//                .hasMessageContaining("Hotel already exist");
////        ConflictException ex = assertThrows(ConflictException.class,
////        	    () -> hotelService.create(createDto));
////        	assertTrue(ex.getMessage().contains("Hotel already exist"));
//        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
//        verifyNoMoreInteractions(hotelRepository);
//        verifyNoInteractions(amenityRepository);
//    }
//
//    // -------------------- update --------------------
//
//    @Test
//    @DisplayName("update: when hotel missing -> throws ResourceNotFoundException")
//    void update_notFound_throws() {
//        when(hotelRepository.findById(999)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> hotelService.update(999, updateDto))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Hotel doesn't exist");
//
//        verify(hotelRepository).findById(999);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("update: conflict && not same -> throws ConflictException")
//    void update_conflictDifferent_throws() {
//        Hotel existing = hotel(11, "Old Name", "Old Location", "desc");
//        when(hotelRepository.findById(11)).thenReturn(Optional.of(existing));
//
//        // Simulate another hotel with the same name/location as dto
//        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
//                .thenReturn(true);
//
//        assertThatThrownBy(() -> hotelService.update(11, updateDto))
//                .isInstanceOf(ConflictException.class)
//                .hasMessageContaining("same name/location");
//
//        verify(hotelRepository).findById(11);
//        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("update: conflict==true but same name+location as existing -> allowed, saves")
//    void update_conflictButSame_okAndSaves() {
//        // existing has the same name/location as updateDto
//        Hotel existing = hotel(12, "The Grand", "Bangalore", "old");
//        when(hotelRepository.findById(12)).thenReturn(Optional.of(existing));
//
//        // Repository says “some” hotel exists with this name/location (could be itself)
//        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
//                .thenReturn(true);
//
//        // After mapper update, save returns updated entity
//        Hotel updated = hotel(12, "The Grand", "Bangalore", "Updated desc");
//        when(hotelRepository.save(existing)).thenReturn(updated);
//
//        HotelResponseDTO resp = hotelService.update(12, updateDto);
//
//        assertThat(resp.getId()).isEqualTo(12);
//        assertThat(resp.getName()).isEqualTo("The Grand");
//        assertThat(resp.getLocation()).isEqualTo("Bangalore");
//        assertThat(resp.getDescription()).isEqualTo("Updated desc");
//
//        verify(hotelRepository).findById(12);
//        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
//        verify(hotelRepository).save(existing);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    // -------------------- getAll --------------------
//
//    @Test
//    @DisplayName("getAll: when list empty -> throws EmptyListException")
//    void getAll_empty_throws() {
//        when(hotelRepository.findAll()).thenReturn(List.of());
//
//        assertThatThrownBy(() -> hotelService.getAll())
//                .isInstanceOf(EmptyListException.class)
//                .hasMessageContaining("empty");
//
//        verify(hotelRepository).findAll();
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("getAll: returns mapped list")
//    void getAll_ok_returnsList() {
//        List<Hotel> hotels = List.of(
//                hotel(1, "A", "BLR", "d1"),
//                hotel(2, "B", "BLR", "d2")
//        );
//        when(hotelRepository.findAll()).thenReturn(hotels);
//
//        List<HotelResponseDTO> resp = hotelService.getAll();
//
//        assertThat(resp).hasSize(2);
//        assertThat(resp.get(0).getId()).isEqualTo(1);
//        assertThat(resp.get(1).getName()).isEqualTo("B");
//
//        verify(hotelRepository).findAll();
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    // -------------------- getById --------------------
//
//    @Test
//    @DisplayName("getById: when found -> returns DTO")
//    void getById_found_ok() {
//        Hotel h = hotel(21, "One", "BLR", "desc");
//        when(hotelRepository.findById(21)).thenReturn(Optional.of(h));
//
//        HotelResponseDTO resp = hotelService.getById(21);
//
//        assertThat(resp.getId()).isEqualTo(21);
//        assertThat(resp.getName()).isEqualTo("One");
//        verify(hotelRepository).findById(21);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("getById: when missing -> throws ResourceNotFoundException")
//    void getById_missing_throws() {
//        when(hotelRepository.findById(22)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> hotelService.getById(22))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("doesn't exist");
//
//        verify(hotelRepository).findById(22);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    // -------------------- getByAmenity --------------------
//
//    @Test
//    @DisplayName("getByAmenity: when amenity id not found -> throws ResourceNotFoundException")
//    void getByAmenity_amenityNotFound_throws() {
//        when(amenityRepository.findById(7)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> hotelService.getByAmenity(7))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Amenity not found");
//
//        verify(amenityRepository).findById(7);
//        verifyNoMoreInteractions(amenityRepository);
//        verifyNoInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("getByAmenity: amenity exists but no hotels -> throws EmptyListException")
//    void getByAmenity_noHotels_throws() {
//        Amenity amenity = mock(Amenity.class);
//        when(amenityRepository.findById(8)).thenReturn(Optional.of(amenity));
//        when(hotelRepository.findHotelsByAmenity(8)).thenReturn(List.of());
//
//        assertThatThrownBy(() -> hotelService.getByAmenity(8))
//                .isInstanceOf(EmptyListException.class)
//                .hasMessageContaining("No hotel is found");
//
//        verify(amenityRepository).findById(8);
//        verify(hotelRepository).findHotelsByAmenity(8);
//        verifyNoMoreInteractions(amenityRepository, hotelRepository);
//    }
//
//    @Test
//    @DisplayName("getByAmenity: returns mapped hotels when present")
//    void getByAmenity_ok_returnsList() {
//        Amenity amenity = mock(Amenity.class);
//        when(amenityRepository.findById(9)).thenReturn(Optional.of(amenity));
//
//        List<Hotel> hotels = List.of(
//                hotel(31, "AmenityHotel1", "BLR", "x"),
//                hotel(32, "AmenityHotel2", "BLR", "y")
//        );
//        when(hotelRepository.findHotelsByAmenity(9)).thenReturn(hotels);
//
//        List<HotelResponseDTO> resp = hotelService.getByAmenity(9);
//
//        assertThat(resp).hasSize(2);
//        assertThat(resp.get(0).getId()).isEqualTo(31);
//        assertThat(resp.get(1).getName()).isEqualTo("AmenityHotel2");
//
//        verify(amenityRepository).findById(9);
//        verify(hotelRepository).findHotelsByAmenity(9);
//        verifyNoMoreInteractions(amenityRepository, hotelRepository);
//    }
//
//    // -------------------- delete --------------------
//
//    @Test
//    @DisplayName("delete: when hotel missing -> throws ResourceNotFoundException")
//    void delete_missing_throws() {
//        when(hotelRepository.existsById(55)).thenReturn(false);
//
//        assertThatThrownBy(() -> hotelService.delete(55))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("doesn't exist");
//
//        verify(hotelRepository).existsById(55);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//
//    @Test
//    @DisplayName("delete: when exists -> calls deleteById")
//    void delete_exists_deletes() {
//        when(hotelRepository.existsById(56)).thenReturn(true);
//
//        hotelService.delete(56);
//
//        verify(hotelRepository).existsById(56);
//        verify(hotelRepository).deleteById(56);
//        verifyNoMoreInteractions(hotelRepository);
//    }
//}
package com.hotelManagement.system;

import com.hotelManagement.system.dto.HotelCreateDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import com.hotelManagement.system.service.HotelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock private HotelRepository hotelRepository;
    @Mock private AmenityRepository amenityRepository;

    @InjectMocks
    private HotelService hotelService;

    private HotelCreateDTO createDto;
    private HotelUpdateDTO updateDto;

    @BeforeEach
    void setUp() {
        createDto = new HotelCreateDTO();
        createDto.setName("The Grand");
        createDto.setLocation("Bangalore");
        createDto.setDescription("Business hotel");

        updateDto = new HotelUpdateDTO();
        updateDto.setName("The Grand");
        updateDto.setLocation("Bangalore");
        updateDto.setDescription("Updated desc");
    }

    private static Hotel hotel(Integer id, String name, String loc, String desc) {
        Hotel h = new Hotel();
        h.setHotelId(id);
        h.setName(name);
        h.setLocation(loc);
        h.setDescription(desc);
        return h;
    }

    // -------------------- create --------------------

    @Test
    @DisplayName("create: unique hotel → saves & returns DTO")
    void create_unique_saves() {
        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
                .thenReturn(false);

        Hotel saved = hotel(101, "The Grand", "Bangalore", "Business hotel");

        when(hotelRepository.save(any(Hotel.class))).thenReturn(saved);

        HotelResponseDTO resp = hotelService.create(createDto);

        assertNotNull(resp);
        assertEquals(101, resp.getId());
        assertEquals("The Grand", resp.getName());
        assertEquals("Bangalore", resp.getLocation());
        assertEquals("Business hotel", resp.getDescription());

        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
        verify(hotelRepository).save(any(Hotel.class));
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(amenityRepository);
    }

    @Test
    @DisplayName("create: duplicate → ConflictException")
    void create_duplicate_throwsConflict() {
        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
                .thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> hotelService.create(createDto));

        assertTrue(ex.getMessage().contains("Hotel already exist"));

        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(amenityRepository);
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("update: hotel not found → ResourceNotFoundException")
    void update_notFound_throws() {
        when(hotelRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> hotelService.update(999, updateDto));

        assertTrue(ex.getMessage().contains("Hotel doesn't exist"));

        verify(hotelRepository).findById(999);
        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    @DisplayName("update: conflict (different existing hotel) → ConflictException")
    void update_conflictDifferent_throws() {
        Hotel existing = hotel(11, "Old Name", "Old Location", "desc");
        when(hotelRepository.findById(11)).thenReturn(Optional.of(existing));

        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
                .thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> hotelService.update(11, updateDto));

        assertTrue(ex.getMessage().contains("same name/location"));

        verify(hotelRepository).findById(11);
        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
    }

    @Test
    @DisplayName("update: conflict == true but same as existing → allowed & saved")
    void update_conflictButSame_ok() {
        Hotel existing = hotel(12, "The Grand", "Bangalore", "old");
        when(hotelRepository.findById(12)).thenReturn(Optional.of(existing));

        when(hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore"))
                .thenReturn(true);

        Hotel updated = hotel(12, "The Grand", "Bangalore", "Updated desc");
        when(hotelRepository.save(existing)).thenReturn(updated);

        HotelResponseDTO resp = hotelService.update(12, updateDto);

        assertEquals(12, resp.getId());
        assertEquals("The Grand", resp.getName());
        assertEquals("Bangalore", resp.getLocation());
        assertEquals("Updated desc", resp.getDescription());

        verify(hotelRepository).findById(12);
        verify(hotelRepository).existsByNameIgnoreCaseAndLocationIgnoreCase("The Grand", "Bangalore");
        verify(hotelRepository).save(existing);
    }

    // -------------------- getAll --------------------

    @Test
    @DisplayName("getAll: empty → EmptyListException")
    void getAll_empty_throws() {
        when(hotelRepository.findAll()).thenReturn(List.of());

        assertThrows(EmptyListException.class,
                () -> hotelService.getAll());

        verify(hotelRepository).findAll();
    }

    @Test
    @DisplayName("getAll: returns list")
    void getAll_ok() {
        List<Hotel> hotels = List.of(
                hotel(1, "A", "BLR", "d1"),
                hotel(2, "B", "BLR", "d2")
        );

        when(hotelRepository.findAll()).thenReturn(hotels);

        List<HotelResponseDTO> resp = hotelService.getAll();

        assertEquals(2, resp.size());
        assertEquals(1, resp.get(0).getId());
        assertEquals("B", resp.get(1).getName());

        verify(hotelRepository).findAll();
    }

    // -------------------- getById --------------------

    @Test
    @DisplayName("getById: found → DTO")
    void getById_found() {
        Hotel h = hotel(21, "One", "BLR", "desc");
        when(hotelRepository.findById(21)).thenReturn(Optional.of(h));

        HotelResponseDTO resp = hotelService.getById(21);

        assertEquals(21, resp.getId());
        assertEquals("One", resp.getName());

        verify(hotelRepository).findById(21);
    }

    @Test
    @DisplayName("getById: missing → ResourceNotFoundException")
    void getById_missing() {
        when(hotelRepository.findById(22)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.getById(22));

        verify(hotelRepository).findById(22);
    }

    // -------------------- getByAmenity --------------------

    @Test
    @DisplayName("getByAmenity: amenity not found → ResourceNotFoundException")
    void getByAmenity_amenityNotFound() {
        when(amenityRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.getByAmenity(7));

        verify(amenityRepository).findById(7);
    }

    @Test
    @DisplayName("getByAmenity: amenity exists but no hotels → EmptyListException")
    void getByAmenity_noHotels() {
        Amenity a = mock(Amenity.class);
        when(amenityRepository.findById(8)).thenReturn(Optional.of(a));
        when(hotelRepository.findHotelsByAmenity(8)).thenReturn(List.of());

        assertThrows(EmptyListException.class,
                () -> hotelService.getByAmenity(8));

        verify(amenityRepository).findById(8);
        verify(hotelRepository).findHotelsByAmenity(8);
    }

    @Test
    @DisplayName("getByAmenity: returns hotels")
    void getByAmenity_ok() {
        Amenity a = mock(Amenity.class);
        when(amenityRepository.findById(9)).thenReturn(Optional.of(a));

        List<Hotel> hotels = List.of(
                hotel(31, "AmenityHotel1", "BLR", "x"),
                hotel(32, "AmenityHotel2", "BLR", "y")
        );
        when(hotelRepository.findHotelsByAmenity(9)).thenReturn(hotels);

        List<HotelResponseDTO> resp = hotelService.getByAmenity(9);

        assertEquals(2, resp.size());
        assertEquals(31, resp.get(0).getId());
        assertEquals("AmenityHotel2", resp.get(1).getName());
    }

    // -------------------- delete --------------------

    @Test
    @DisplayName("delete: missing → ResourceNotFoundException")
    void delete_missing() {
        when(hotelRepository.existsById(55)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.delete(55));

        verify(hotelRepository).existsById(55);
    }

    @Test
    @DisplayName("delete: exists → deleteById called")
    void delete_ok() {
        when(hotelRepository.existsById(56)).thenReturn(true);

        hotelService.delete(56);

        verify(hotelRepository).existsById(56);
        verify(hotelRepository).deleteById(56);
    }
}