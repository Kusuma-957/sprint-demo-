//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.RoomCreateDTO;
//import com.hotelManagement.system.dto.RoomResponseDTO;
//import com.hotelManagement.system.dto.RoomUpdateDTO;
//import com.hotelManagement.system.entity.Room;
//import com.hotelManagement.system.entity.RoomType;
//import com.hotelManagement.system.entity.Hotel;
//import com.hotelManagement.system.mapper.RoomMapper;
//import com.hotelManagement.system.repository.RoomRepository;
//import com.hotelManagement.system.repository.RoomTypeRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class RoomService {
//
//    private final RoomRepository roomRepository;
//    private final RoomTypeRepository roomTypeRepository;
//    private final HotelRepository hotelRepository;
//
//    public RoomResponseDTO create(RoomCreateDTO dto) {
//
////        Hotel hotel = hotelRepository.findById(dto.getHotelId())
////                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
//
//        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));
//
//        Room room = RoomMapper.toEntity(dto);
//        //room.setHotel(hotel);
//        room.setRoomType(type);
//
//        Room saved = roomRepository.save(room);
//        return RoomMapper.toResponse(saved);
//    }
//
//    public RoomResponseDTO update(Integer roomId, RoomUpdateDTO dto) {
//        Room room = roomRepository.findById(roomId)
//                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
//
//        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));
//
//        RoomMapper.updateEntity(dto, room);
//        room.setRoomType(type);
//
//        return RoomMapper.toResponse(roomRepository.save(room));
//    }
//
//    public List<RoomResponseDTO> getAll() {
//        return RoomMapper.toResponseList(roomRepository.findAll());
//    }
//
//    public RoomResponseDTO getById(Integer roomId) {
//        Room room = roomRepository.findById(roomId)
//                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
//        return RoomMapper.toResponse(room);
//    }
//
//    public List<RoomResponseDTO> getAvailableByType(Integer typeId) {
//        return RoomMapper.toResponseList(roomRepository.findAvailableRoomsByType(typeId));
//    }
//
////    public List<RoomResponseDTO> getByLocation(String location) {
////        return RoomMapper.toResponseList(roomRepository.findRoomsByLocation(location));
////    }
//
//    public List<RoomResponseDTO> getByAmenity(Integer amenityId) {
//        return RoomMapper.toResponseList(roomRepository.findRoomsByAmenity(amenityId));
//    }
//}




package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.entity.Room;
import com.hotelManagement.system.entity.RoomType;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.RoomMapper;
import com.hotelManagement.system.repository.RoomRepository;
import com.hotelManagement.system.repository.RoomTypeRepository;

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

    // POST
    public void create(RoomCreateDTO dto) {

        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            throw new ConflictException("Room already exist");
        }

        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType not found"));

        Room room = RoomMapper.toEntity(dto);
        room.setRoomType(type);

        roomRepository.save(room);
    }

    // GET ALL
    public List<RoomResponseDTO> getAll() {
        List<Room> rooms = roomRepository.findAll();

        if (rooms.isEmpty()) {
            throw new EmptyListException("room list is empty");
        }

        return RoomMapper.toResponseList(rooms);
    }

    // GET BY ID
    public RoomResponseDTO getById(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("room doesn't exist"));
        return RoomMapper.toResponse(room);
    }

    // UPDATE
    public void update(Integer roomId, RoomUpdateDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room doesn't exist"));

        RoomType type = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType not found"));

        RoomMapper.updateEntity(dto, room);
        room.setRoomType(type);

        roomRepository.save(room);
    }

    // GET AVAILABLE BY TYPE
    public List<RoomResponseDTO> getAvailableByType(Integer typeId) {
        List<Room> rooms = roomRepository.findAvailableRoomsByType(typeId);

        if (rooms.isEmpty()) {
            throw new EmptyListException("No room found with given type");
        }

        return RoomMapper.toResponseList(rooms);
    }

    // GET BY AMENITY
    public List<RoomResponseDTO> getByAmenity(Integer amenityId) {
        List<Room> rooms = roomRepository.findRoomsByAmenity(amenityId);

        if (rooms.isEmpty()) {
            throw new EmptyListException("amenity doesn't exist with given id");
        }

        return RoomMapper.toResponseList(rooms);
    }

    // DELETE
    public void delete(Integer roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room doesn't exist");
        }
        roomRepository.deleteById(roomId);
    }
}