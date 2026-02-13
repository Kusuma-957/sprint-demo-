package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.RoomAmenityResponseDTO;

public class RoomAmenityMapper {

    private RoomAmenityMapper() {}

    public static RoomAmenityResponseDTO toResponse(Integer roomId, Integer amenityId) {
        return new RoomAmenityResponseDTO(roomId, amenityId);
    }
}