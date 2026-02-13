package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.entity.Room;

import java.util.List;

public class RoomMapper {

    private RoomMapper() {}

    public static Room toEntity(RoomCreateDTO dto) {
        Room r = new Room();
        r.setRoomNumber(dto.getRoomNumber());
        r.setIsAvailable(dto.getIsAvailable());
        return r;
    }

    public static void updateEntity(RoomUpdateDTO dto, Room room) {
        room.setRoomNumber(dto.getRoomNumber());
        room.setIsAvailable(dto.getIsAvailable());
        room.setRoomType(room.getRoomType());  // this stays as is (fetched separately)
    }

    public static RoomResponseDTO toResponse(Room room) {
        return new RoomResponseDTO(
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType().getRoomTypeId(),
                room.getIsAvailable()
        );
    }
//  room.getHotel().getHotelId(),

    public static List<RoomResponseDTO> toResponseList(List<Room> list) {
        return list.stream().map(RoomMapper::toResponse).toList();
    }
}