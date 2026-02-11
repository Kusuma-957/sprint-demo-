package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.RoomTypeCreateDTO;
import com.hotelManagement.system.dto.RoomTypeResponseDTO;
import com.hotelManagement.system.dto.RoomTypeUpdateDTO;
import com.hotelManagement.system.entity.RoomType;

import java.util.List;

public class RoomTypeMapper {

    private RoomTypeMapper() {}

    public static RoomType toEntity(RoomTypeCreateDTO dto) {
        return RoomType.builder()
                .typeName(dto.getTypeName())
                .maxOccupancy(dto.getMaxOccupancy())
                .pricePerNight(dto.getPricePerNight())
                .description(dto.getDescription())
                .build();
    }

    public static void updateEntity(RoomTypeUpdateDTO dto, RoomType entity) {
        entity.setTypeName(dto.getTypeName());
        entity.setMaxOccupancy(dto.getMaxOccupancy());
        entity.setPricePerNight(dto.getPricePerNight());
        entity.setDescription(dto.getDescription());
    }

    public static RoomTypeResponseDTO toResponse(RoomType r) {
        return new RoomTypeResponseDTO(
                r.getRoomTypeId(),
                r.getTypeName(),
                r.getMaxOccupancy(),
                r.getPricePerNight(),
                r.getDescription()
        );
    }

    public static List<RoomTypeResponseDTO> toResponseList(List<RoomType> list) {
        return list.stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }
}