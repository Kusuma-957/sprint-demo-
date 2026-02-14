//package com.hotelManagement.system.service;
//
//import com.hotelManagement.system.dto.*;
//import com.hotelManagement.system.entity.RoomType;
//import com.hotelManagement.system.mapper.RoomTypeMapper;
//import com.hotelManagement.system.repository.RoomTypeRepository;
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
//public class RoomTypeService {
//
//    private final RoomTypeRepository roomTypeRepository;
//
//    public RoomTypeResponseDTO create(RoomTypeCreateDTO dto) {
//        if (roomTypeRepository.existsByTypeNameIgnoreCase(dto.getTypeName())) {
//            throw new IllegalStateException("RoomType already exists");
//        }
//
//        RoomType type = RoomTypeMapper.toEntity(dto);
//        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
//    }
//
//    public RoomTypeResponseDTO update(Integer id, RoomTypeUpdateDTO dto) {
//        RoomType type = roomTypeRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));
//
//        RoomTypeMapper.updateEntity(dto, type);
//        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
//    }
//
//    public RoomTypeResponseDTO get(Integer id) {
//        RoomType type = roomTypeRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));
//        return RoomTypeMapper.toResponse(type);
//    }
//
//    public List<RoomTypeResponseDTO> getAll() {
//        return RoomTypeMapper.toResponseList(roomTypeRepository.findAll());
//    }
//
//    public void delete(Integer id) {
//        roomTypeRepository.deleteById(id);
//    }
//}


package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.entity.RoomType;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.RoomTypeMapper;
import com.hotelManagement.system.repository.RoomTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeResponseDTO create(RoomTypeCreateDTO dto) {
        // ADDFAILS on conflict
        if (roomTypeRepository.existsByTypeNameIgnoreCase(dto.getTypeName())) {
            throw new ConflictException("RoomType already exist");
        }

        RoomType type = RoomTypeMapper.toEntity(dto);
        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
    }

    public RoomTypeResponseDTO update(Integer id, RoomTypeUpdateDTO dto) {
        // UPDTFAILS if not found
        RoomType type = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType doesn't exist"));

        RoomTypeMapper.updateEntity(dto, type);
        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
    }

    public RoomTypeResponseDTO get(Integer id) {
        // GETFAILS if not found
        RoomType type = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType doesn't exist"));
        return RoomTypeMapper.toResponse(type);
    }

    public List<RoomTypeResponseDTO> getAll() {
        return RoomTypeMapper.toResponseList(roomTypeRepository.findAll());
    }

    public void delete(Integer id) {
        // DLTFAILS if not found
        if (!roomTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("RoomType doesn't exist");
        }
        roomTypeRepository.deleteById(id);
    }
}