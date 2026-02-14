//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.HotelAmenityCreateDTO;
//import com.hotelManagement.system.dto.HotelAmenityResponseDTO;
//import com.hotelManagement.system.entity.Hotel;
//import com.hotelManagement.system.entity.Amenity;
//import com.hotelManagement.system.mapper.HotelAmenityMapper;
//import com.hotelManagement.system.repository.HotelAmenityRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//import com.hotelManagement.system.repository.AmenityRepository;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class HotelAmenityService {
//
//    private final HotelAmenityRepository hotelAmenityRepository;
//    private final HotelRepository hotelRepository;
//    private final AmenityRepository amenityRepository;
//
//    public HotelAmenityResponseDTO createMapping(HotelAmenityCreateDTO dto) {
//
//        // Validate hotel exists
//        Hotel hotel = hotelRepository.findById(dto.getHotelId())
//                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + dto.getHotelId()));
//
//        // Validate amenity exists
//        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
//                .orElseThrow(() -> new IllegalArgumentException("Amenity not found: " + dto.getAmenityId()));
//
//        // Prevent duplicate mapping
//        if (hotelAmenityRepository.existsMapping(hotel.getHotelId(), amenity.getAmenityId())) {
//            throw new IllegalStateException("Mapping already exists");
//        }
//
//        hotelAmenityRepository.insertMapping(hotel.getHotelId(), amenity.getAmenityId());
//
//        return HotelAmenityMapper.toResponse(hotel.getHotelId(), amenity.getAmenityId());
//    }
//}



package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.HotelAmenityCreateDTO;
import com.hotelManagement.system.dto.HotelAmenityResponseDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.HotelAmenityMapper;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelAmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelAmenityService {

    private final HotelAmenityRepository hotelAmenityRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    /**
     * Creates a Hotel-Amenity mapping.
     * - 404 if Hotel or Amenity doesn't exist (ResourceNotFoundException)
     * - 409 if mapping already exists (ConflictException)
     */
    public HotelAmenityResponseDTO createMapping(HotelAmenityCreateDTO dto) {

        // Validate hotel exists
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + dto.getHotelId()));

        // Validate amenity exists
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found: " + dto.getAmenityId()));

        // Prevent duplicate mapping
        boolean mappingExists = hotelAmenityRepository
                .existsMapping(hotel.getHotelId(), amenity.getAmenityId());

        if (mappingExists) {
            throw new ConflictException("Hotel amenity already exist");
        }

        // Persist mapping
        hotelAmenityRepository.insertMapping(hotel.getHotelId(), amenity.getAmenityId());

        // Map to response
        return HotelAmenityMapper.toResponse(hotel.getHotelId(), amenity.getAmenityId());
    }
}