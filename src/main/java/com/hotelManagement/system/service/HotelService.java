//package com.hotelManagement.system.service;
// 
//import com.hotelManagement.system.dto.HotelCreateDTO;
//import com.hotelManagement.system.dto.HotelResponseDTO;
//import com.hotelManagement.system.dto.HotelUpdateDTO;
//import com.hotelManagement.system.entity.Hotel;
//import com.hotelManagement.system.mapper.HotelMapper;
//import com.hotelManagement.system.repository.AmenityRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
// 
//import java.util.List;
// 
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class HotelService {
// 
//    private final HotelRepository hotelRepository;
//    private final AmenityRepository amenityRepository;
// 
//    // ---- Commands ----
// 
//    public HotelResponseDTO create(HotelCreateDTO dto) {
//        boolean exists = hotelRepository
//                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
//        if (exists) {
//            throw new IllegalArgumentException("Hotel already exists at this location");
//        }
// 
//        Hotel hotel = HotelMapper.toEntity(dto);
//        Hotel saved = hotelRepository.save(hotel);
//        return HotelMapper.toResponse(saved);
//    }
// 
//    public HotelResponseDTO update(Integer hotelId, HotelUpdateDTO dto) {
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
// 
//        boolean conflict = hotelRepository
//                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
//        boolean nameSame = dto.getName().equalsIgnoreCase(hotel.getName());
//        boolean locSame  = dto.getLocation().equalsIgnoreCase(hotel.getLocation());
//        if (conflict && !(nameSame && locSame)) {
//            throw new IllegalStateException("Another hotel with same name/location already exists");
//        }
// 
//        HotelMapper.updateEntity(dto, hotel);
//        Hotel updated = hotelRepository.save(hotel);
//        return HotelMapper.toResponse(updated);
//    }
// 
//    // ---- Queries ----
// 
//    public List<HotelResponseDTO> getAll() {
//        return HotelMapper.toResponseList(hotelRepository.findAll());
//    }
// 
//    public HotelResponseDTO getById(Integer hotelId) {
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
//        return HotelMapper.toResponse(hotel);
//    }
// 
//    public List<HotelResponseDTO> getByAmenity(Integer amenityId) {
// 
//        amenityRepository.findById(amenityId)
//            .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
// 
//        return HotelMapper.toResponseList(
//            hotelRepository.findHotelsByAmenity(amenityId)
//        );
//    }
//}
package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.HotelCreateDTO;

import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.HotelMapper;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    // -------------------- Commands --------------------

    /**
     * Create a hotel enforcing uniqueness on (name, location).
     * Throws ConflictException if a hotel already exists at the location with same name.
     */
    public HotelResponseDTO create(HotelCreateDTO dto) {
        boolean exists = hotelRepository
                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
        if (exists) {
            // GlobalExceptionHandler maps this to 409 CONFLICT with code ADDFAILS
            throw new ConflictException("Hotel already exist");
        }

        Hotel hotel = HotelMapper.toEntity(dto);
        Hotel saved = hotelRepository.save(hotel);
        return HotelMapper.toResponse(saved);
    }

    /**
     * Update a hotel. Re-check uniqueness when name/location change.
     * - Not found -> ResourceNotFoundException (maps to UPDTFAILS on /update/*)
     * - Duplicate -> ConflictException (maps to ADDFAILS 409)
     */
    public HotelResponseDTO update(Integer hotelId, HotelUpdateDTO dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel doesn't exist"));

        boolean conflict = hotelRepository
                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());

        boolean same = dto.getName().equalsIgnoreCase(hotel.getName())
                && dto.getLocation().equalsIgnoreCase(hotel.getLocation());

        if (conflict && !same) {
            throw new ConflictException("Another hotel with same name/location already exists");
        }

        HotelMapper.updateEntity(dto, hotel);
        Hotel updated = hotelRepository.save(hotel);
        return HotelMapper.toResponse(updated);
    }

    // -------------------- Queries --------------------

    /**
     * Get all hotels.
     * If you need empty-list as error per CSV, throw EmptyListException.
     */
    public List<HotelResponseDTO> getAll() {
        List<Hotel> hotels = hotelRepository.findAll();
        if (hotels.isEmpty()) {
            // GlobalExceptionHandler -> code GETALLFAILS (404)
            throw new EmptyListException("Hotel list is empty");
        }
        return HotelMapper.toResponseList(hotels);
    }

    /**
     * Get hotel by id.
     * Not found -> ResourceNotFoundException -> GETFAILS (404)
     */
    public HotelResponseDTO getById(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel doesn't exist"));
        return HotelMapper.toResponse(hotel);
    }

    /**
     * Get hotels that provide a specific amenity.
     * - If amenity id does not exist -> ResourceNotFoundException (GETFAILS 404).
     * - If amenity exists but no hotels -> EmptyListException (GETALLFAILS 404).
     *
     * Backed by schema: Hotel <-> HotelAmenity on hotel_id / amenity_id.
     */
    public List<HotelResponseDTO> getByAmenity(Integer amenityId) {
        amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found: " + amenityId));

        List<Hotel> hotels = hotelRepository.findHotelsByAmenity(amenityId);
        if (hotels.isEmpty()) {
            throw new EmptyListException("No hotel is found with the specified amenity");
        }
        return HotelMapper.toResponseList(hotels);
    }
    
    
    public void delete(Integer hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            // Same style you used elsewhere: IllegalArgumentException
            throw new ResourceNotFoundException("Hotel doesn't exist");
        }
        hotelRepository.deleteById(hotelId);
    }
 
}
