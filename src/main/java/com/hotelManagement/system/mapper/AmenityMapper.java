package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.entity.Amenity;

import java.util.List;

public class AmenityMapper {

    public static Amenity toEntity(AmenityCreateDTO dto) {
        Amenity a = new Amenity();
        a.setName(dto.getName());
        a.setDescription(dto.getDescription());
        return a;
    }

    public static void updateEntity(AmenityUpdateDTO dto, Amenity entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }

    public static AmenityResponseDTO toResponse(Amenity a) {
        return new AmenityResponseDTO(
                a.getAmenityId(),
                a.getName(),
                a.getDescription()
        );
    }

    public static List<AmenityResponseDTO> toResponseList(List<Amenity> list) {
        return list.stream().map(AmenityMapper::toResponse).toList();
    }
}