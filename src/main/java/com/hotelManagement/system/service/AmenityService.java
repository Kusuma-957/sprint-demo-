//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.*;
//import com.hotelManagement.system.entity.Amenity;
//import com.hotelManagement.system.exception.ResourceNotFoundException;
//import com.hotelManagement.system.mapper.AmenityMapper;
//import com.hotelManagement.system.repository.AmenityRepository;
//import com.hotelManagement.system.repository.HotelRepository;
//import com.hotelManagement.system.repository.RoomRepository;
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
//public class AmenityService {
//
//	private final AmenityRepository amenityRepository;
//	private final RoomRepository roomRepository;  // ← REQUIRED
//	private final HotelRepository hotelRepository;
//
//	public AmenityResponseDTO create(AmenityCreateDTO dto) {
//		if (amenityRepository.existsByNameIgnoreCase(dto.getName())) {
//			throw new IllegalArgumentException("Amenity already exists");
//		}
//
//		Amenity entity = AmenityMapper.toEntity(dto);
//		return AmenityMapper.toResponse(amenityRepository.save(entity));
//	}
//
//	public AmenityResponseDTO update(Integer id, AmenityUpdateDTO dto) {
//		Amenity entity = amenityRepository.findById(id)
//				.orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
//
//		AmenityMapper.updateEntity(dto, entity);
//		return AmenityMapper.toResponse(amenityRepository.save(entity));
//	}
//
//	public AmenityResponseDTO get(Integer id) {
//		Amenity a = amenityRepository.findById(id)
//				.orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
//		return AmenityMapper.toResponse(a);
//	}
//
//	public List<AmenityResponseDTO> getAll() {
//		return AmenityMapper.toResponseList(amenityRepository.findAll());
//	}
//
//	public void delete(Integer id) {
//
//		boolean exists = amenityRepository.existsById(id);
//		if (!exists) {
//			// GlobalExceptionHandler will map DELETE + not found -> code DLTFAILS, HTTP 404
//			throw new ResourceNotFoundException("Amenity doesn't exist");
//		}
//
//		amenityRepository.deleteById(id);
//	}
//
//
//	public List<AmenityResponseDTO> getByRoomId(Integer roomId) {
//		// Check if room exists
//		if (!roomRepository.existsById(roomId)) {
//			throw new IllegalArgumentException("room not found with given room id");
//		}
//		// Fetch amenities for that room
//		List<Amenity> amenities = amenityRepository.findByRoomId(roomId);
//		return AmenityMapper.toResponseList(amenities);   
//	}
//
//	public List<AmenityResponseDTO> getByHotelId(Integer hotelId) {
//		// Check if hotel exists
//		if (!hotelRepository.existsById(hotelId)) {
//			throw new IllegalArgumentException("hotel not found with given hotel id");
//		}
//		// Fetch amenities
//		List<Amenity> amenities = amenityRepository.findByHotelId(hotelId);
//		return AmenityMapper.toResponseList(amenities);
//	}  
//}


package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.AmenityCreateDTO;
import com.hotelManagement.system.dto.AmenityResponseDTO;
import com.hotelManagement.system.dto.AmenityUpdateDTO;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.AmenityMapper;
import com.hotelManagement.system.repository.AmenityRepository;
import com.hotelManagement.system.repository.HotelRepository;
import com.hotelManagement.system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    // -------- Commands --------

    public AmenityResponseDTO create(AmenityCreateDTO dto) {
        if (amenityRepository.existsByNameIgnoreCase(dto.getName())) {
            // 409 with code ADDFAILS by GlobalExceptionHandler
            throw new ConflictException("Amenity already exists");
        }
        Amenity entity = AmenityMapper.toEntity(dto);
        return AmenityMapper.toResponse(amenityRepository.save(entity));
    }

    public AmenityResponseDTO update(Integer id, AmenityUpdateDTO dto) {
        Amenity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity doesn't exist"));

        // If you also want to prevent name duplication on update:
        if (dto.getName() != null
                && !dto.getName().equalsIgnoreCase(entity.getName())
                && amenityRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Another amenity with same name already exists");
        }

        AmenityMapper.updateEntity(dto, entity);
        return AmenityMapper.toResponse(amenityRepository.save(entity));
    }

    public void delete(Integer id) {
        boolean exists = amenityRepository.existsById(id);
        if (!exists) {
            // DELETE + not found → code DLTFAILS (mapped in handler)
            throw new ResourceNotFoundException("Amenity doesn't exist");
        }
        amenityRepository.deleteById(id);
    }

    // -------- Queries --------

    public AmenityResponseDTO get(Integer id) {
        Amenity a = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity doesn't exist"));
        return AmenityMapper.toResponse(a);
    }

    public List<AmenityResponseDTO> getAll() {
        List<Amenity> list = amenityRepository.findAll();
        // If CSV requires empty-list to be an error, uncomment:
        // if (list.isEmpty()) {
        //     throw new EmptyListException("Amenity list is empty");
        // }
        return AmenityMapper.toResponseList(list);
    }

    public List<AmenityResponseDTO> getByRoomId(Integer roomId) {
        // Check if room exists
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with the given roomId");
        }
        // Fetch amenities for that room
        List<Amenity> amenities = amenityRepository.findByRoomId(roomId);

        // If “no amenities” should be an error:
        // if (amenities.isEmpty()) {
        //     throw new EmptyListException("No amenities found for the given room");
        // }

        return AmenityMapper.toResponseList(amenities);
    }

    public List<AmenityResponseDTO> getByHotelId(Integer hotelId) {
        // Check if hotel exists
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel not found with the given hotelId");
        }
        // Fetch amenities
        List<Amenity> amenities = amenityRepository.findByHotelId(hotelId);

        // If “no amenities” should be an error:
        // if (amenities.isEmpty()) {
        //     throw new EmptyListException("No amenities found for the given hotel");
        // }

        return AmenityMapper.toResponseList(amenities);
    }
}