package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.HotelAmenityCreateDTO;
import com.hotelManagement.system.dto.HotelAmenityResponseDTO;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.mapper.HotelAmenityMapper;
import com.hotelManagement.system.repository.HotelAmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import com.hotelManagement.system.repository.AmenityRepository;

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

    public HotelAmenityResponseDTO createMapping(HotelAmenityCreateDTO dto) {

        // Validate hotel exists
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + dto.getHotelId()));

        // Validate amenity exists
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new IllegalArgumentException("Amenity not found: " + dto.getAmenityId()));

        // Prevent duplicate mapping
        if (hotelAmenityRepository.existsMapping(hotel.getHotelId(), amenity.getAmenityId())) {
            throw new IllegalStateException("Mapping already exists");
        }

        hotelAmenityRepository.insertMapping(hotel.getHotelId(), amenity.getAmenityId());

        return HotelAmenityMapper.toResponse(hotel.getHotelId(), amenity.getAmenityId());
    }
}