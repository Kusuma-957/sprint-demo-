//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.entity.Hotel;
//import com.hotelManagement.system.dto.HotelCreateDTO;
//import com.hotelManagement.system.dto.HotelResponseDTO;
//import com.hotelManagement.system.dto.HotelUpdateDTO;
//import com.hotelManagement.system.repository.AmenityRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//import com.hotelManagement.system.exception.ConflictException;
//import com.hotelManagement.system.exception.ResourceNotFoundException;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.util.List;
////
////@Service
////@RequiredArgsConstructor
////@Transactional
////public class HotelService implements HotelRepository {
////
////    private final HotelRepository hotelRepository;
////    private final AmenityRepository amenityRepository;
////
////    @Override
////    public HotelResponseDTO create(HotelCreateDTO dto) {
////        // Uniqueness rule (example): name + location should be unique
////        boolean exists = hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase(
////                dto.getName(), dto.getLocation());
////        if (exists) {
////            throw new ConflictException("Hotel already exists at the given location");
////        }
////
////        Hotel hotel = new Hotel();
////        hotel.setName(dto.getName());
////        hotel.setLocation(dto.getLocation());
////        hotel.setDescription(dto.getDescription());
////
////        Hotel saved = hotelRepository.save(hotel);
////        return toResponse(saved);
////    }
////
////    @Override
////    public List<HotelResponseDTO> getAll() {
////        return hotelRepository.findAll().stream().map(this::toResponse).toList();
////    }
////
////    @Override
////    public HotelResponseDTO getById(Integer hotelId) {
////        Hotel hotel = hotelRepository.findById(hotelId)
////                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
////        return toResponse(hotel);
////    }
////
////    @Override
////    public List<HotelResponseDTO> getByAmenity(Integer amenityId) {
////        // Optional: validate amenity exists (clearer 404 if invalid id)
////        amenityRepository.findById(amenityId)
////                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found: " + amenityId));
////
////        return hotelRepository.findDistinctByAmenityId(amenityId)
////                .stream().map(this::toResponse).toList();
////    }
////
////    @Override
////    public HotelResponseDTO update(Integer hotelId, HotelUpdateDTO dto) {
////        Hotel hotel = hotelRepository.findById(hotelId)
////                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
////
////        // If name/location changed, check uniqueness
////        boolean conflict = hotelRepository.existsByNameIgnoreCaseAndLocationIgnoreCase(
////                dto.getName(), dto.getLocation());
////        if (conflict && !(dto.getName().equalsIgnoreCase(hotel.getName())
////                && dto.getLocation().equalsIgnoreCase(hotel.getLocation()))) {
////            throw new ConflictException("Another hotel with the same name/location already exists");
////        }
////
////        hotel.setName(dto.getName());
////        hotel.setLocation(dto.getLocation());
////        hotel.setDescription(dto.getDescription());
////
////        return toResponse(hotelRepository.save(hotel));
////    }
////
////    private HotelResponseDTO toResponse(Hotel h) {
////        return new HotelResponseDTO(h.getId(), h.getName(), h.getLocation(), h.getDescription());
////    }
////}
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class HotelService {
//
//    private final HotelRepository hotelRepository;
//    private final AmenityRepository amenityRepository;
//    public HotelResponseDTO create(HotelCreateDTO dto) {
//        // uniqueness example: (name + location)
//        boolean exists = hotelRepository
//                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
//        if (exists) {
//            throw new IllegalArgumentException("Hotel already exists at this location");
//        }
//        Hotel hotel = new Hotel();
//        hotel.setName(dto.getName());
//        hotel.setLocation(dto.getLocation());
//        hotel.setDescription(dto.getDescription());
//
//        return toResponse(hotelRepository.save(hotel));
//    }
//
//    public HotelResponseDTO update(Integer hotelId, HotelUpdateDTO dto) {
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
//
//        // if either changed, re-check uniqueness
//        boolean conflict = hotelRepository
//                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
//        boolean nameSame = dto.getName().equalsIgnoreCase(hotel.getName());
//        boolean locSame  = dto.getLocation().equalsIgnoreCase(hotel.getLocation());
//        if (conflict && !(nameSame && locSame)) {
//            throw new IllegalStateException("Another hotel with same name/location already exists");
//        }
//
//        hotel.setName(dto.getName());
//        hotel.setLocation(dto.getLocation());
//        hotel.setDescription(dto.getDescription());
//
//        return toResponse(hotelRepository.save(hotel));
//    }
//
//    // ---- Queries ----
//
//    public List<HotelResponseDTO> getAll() {
//        return hotelRepository.findAll().stream().map(this::toResponse).toList();
//    }
//
//    public HotelResponseDTO getById(Integer hotelId) {
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
//        return toResponse(hotel);
//    }
//
//    public List<HotelResponseDTO> getByAmenity(Integer amenityId) {
//        // Validate amenity exists for clearer 404/400 behavior
//        amenityRepository.findById(amenityId)
//                .orElseThrow(() -> new IllegalArgumentException("Amenity not found: " + amenityId));
//
//        return hotelRepository.findDistinctByAmenityId(amenityId)
//                .stream().map(this::toResponse).toList();
//    }
//
//    // ---- Mapper ----
//
//    private HotelResponseDTO toResponse(Hotel h) {
//        return new HotelResponseDTO(h.getId(), h.getName(), h.getLocation(), h.getDescription());
//    }
//}




package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.HotelCreateDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.entity.Hotel;
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

    // ---- Commands ----

    public HotelResponseDTO create(HotelCreateDTO dto) {
        boolean exists = hotelRepository
                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
        if (exists) {
            throw new IllegalArgumentException("Hotel already exists at this location");
        }

        Hotel hotel = HotelMapper.toEntity(dto);
        Hotel saved = hotelRepository.save(hotel);
        return HotelMapper.toResponse(saved);
    }

    public HotelResponseDTO update(Integer hotelId, HotelUpdateDTO dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));

        boolean conflict = hotelRepository
                .existsByNameIgnoreCaseAndLocationIgnoreCase(dto.getName(), dto.getLocation());
        boolean nameSame = dto.getName().equalsIgnoreCase(hotel.getName());
        boolean locSame  = dto.getLocation().equalsIgnoreCase(hotel.getLocation());
        if (conflict && !(nameSame && locSame)) {
            throw new IllegalStateException("Another hotel with same name/location already exists");
        }

        HotelMapper.updateEntity(dto, hotel);
        Hotel updated = hotelRepository.save(hotel);
        return HotelMapper.toResponse(updated);
    }

    // ---- Queries ----

    public List<HotelResponseDTO> getAll() {
        return HotelMapper.toResponseList(hotelRepository.findAll());
    }

    public HotelResponseDTO getById(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
        return HotelMapper.toResponse(hotel);
    }

    public List<HotelResponseDTO> getByAmenity(Integer amenityId) {

        amenityRepository.findById(amenityId)
            .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));

        return HotelMapper.toResponseList(
            hotelRepository.findHotelsByAmenity(amenityId)
        );
    }
}