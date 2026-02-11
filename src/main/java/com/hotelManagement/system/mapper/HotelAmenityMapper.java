package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.HotelAmenityResponseDTO;

public class HotelAmenityMapper {

    private HotelAmenityMapper() {}

    public static HotelAmenityResponseDTO toResponse(Integer hotelId, Integer amenityId) {
        return new HotelAmenityResponseDTO(hotelId, amenityId);
    }
}