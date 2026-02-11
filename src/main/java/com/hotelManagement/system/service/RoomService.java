package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.entity.RoomType;
import com.hotelManagement.system.entity.Hotel;
import com.hotelManagement.system.mapper.RoomMapper;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.repository.RoomTypeRepository;
import com.hotelManagement.system.repository.HotelRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    public RoomResponseDTO create(RoomCreateDTO dto) {

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));

        Room room = RoomMapper.toEntity(dto);
        room.setHotel(hotel);
        room.setRoomType(type);

        Room saved = roomRepository.save(room);
        return RoomMapper.toResponse(saved);
    }

    public RoomResponseDTO update(Integer roomId, RoomUpdateDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));

        RoomMapper.updateEntity(dto, room);
        room.setRoomType(type);

        return RoomMapper.toResponse(roomRepository.save(room));
    }

    public List<RoomResponseDTO> getAll() {
        return RoomMapper.toResponseList(roomRepository.findAll());
    }

    public RoomResponseDTO getById(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return RoomMapper.toResponse(room);
    }

    public List<RoomResponseDTO> getAvailableByType(Integer typeId) {
        return RoomMapper.toResponseList(roomRepository.findAvailableRoomsByType(typeId));
    }

    public List<RoomResponseDTO> getByLocation(String location) {
        return RoomMapper.toResponseList(roomRepository.findRoomsByLocation(location));
    }

    public List<RoomResponseDTO> getByAmenity(Integer amenityId) {
        return RoomMapper.toResponseList(roomRepository.findRoomsByAmenity(amenityId));
    }
}