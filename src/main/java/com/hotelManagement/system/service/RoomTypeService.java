package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.entity.RoomType;
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
        if (roomTypeRepository.existsByTypeNameIgnoreCase(dto.getTypeName())) {
            throw new IllegalStateException("RoomType already exists");
        }

        RoomType type = RoomTypeMapper.toEntity(dto);
        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
    }

    public RoomTypeResponseDTO update(Integer id, RoomTypeUpdateDTO dto) {
        RoomType type = roomTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));

        RoomTypeMapper.updateEntity(dto, type);
        return RoomTypeMapper.toResponse(roomTypeRepository.save(type));
    }

    public RoomTypeResponseDTO get(Integer id) {
        RoomType type = roomTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RoomType not found"));
        return RoomTypeMapper.toResponse(type);
    }

    public List<RoomTypeResponseDTO> getAll() {
        return RoomTypeMapper.toResponseList(roomTypeRepository.findAll());
    }

    public void delete(Integer id) {
        roomTypeRepository.deleteById(id);
    }
}