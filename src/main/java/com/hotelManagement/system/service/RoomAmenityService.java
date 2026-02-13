package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.RoomAmenityCreateDTO;
import com.hotelManagement.system.dto.RoomAmenityResponseDTO;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.mapper.RoomAmenityMapper;
import com.hotelManagement.system.repository.RoomAmenityRepository;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.repository.AmenityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomAmenityService {

    private final RoomAmenityRepository roomAmenityRepository;
    private final RoomRepository roomRepository;
    private final AmenityRepository amenityRepository;

    public RoomAmenityResponseDTO createMapping(RoomAmenityCreateDTO dto) {

        // Validate Room exists
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.getRoomId()));

        // Validate Amenity exists
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new IllegalArgumentException("Amenity not found: " + dto.getAmenityId()));

        // Prevent duplicate mapping
        if (roomAmenityRepository.existsMapping(room.getRoomId(), amenity.getAmenityId())) {
            throw new IllegalStateException("Mapping already exists");
        }

        roomAmenityRepository.insertMapping(room.getRoomId(), amenity.getAmenityId());

        return RoomAmenityMapper.toResponse(room.getRoomId(), amenity.getAmenityId());
    }
}