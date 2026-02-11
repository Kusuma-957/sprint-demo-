package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.HotelCreateDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.entity.Hotel;

import java.util.Collection;
import java.util.List;

public final class HotelMapper {

    private HotelMapper() {
        // utility class
    }

    // ----- DTO -> Entity -----

    public static Hotel toEntity(HotelCreateDTO dto) {
        if (dto == null) return null;
        Hotel h = new Hotel();
        h.setName(dto.getName());
        h.setLocation(dto.getLocation());
        h.setDescription(dto.getDescription());
        return h;
    }

    public static void updateEntity(HotelUpdateDTO dto, Hotel entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());
    }

    // ----- Entity -> DTO -----

    public static HotelResponseDTO toResponse(Hotel h) {
        if (h == null) return null;
        return new HotelResponseDTO(
                h.getHotelId(),
                h.getName(),
                h.getLocation(),
                h.getDescription()
        );
    }

    public static List<HotelResponseDTO> toResponseList(Collection<Hotel> hotels) {
        return (hotels == null) ? List.of()
                : hotels.stream().map(HotelMapper::toResponse).toList();
    }
}
