package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.entity.Amenity;
import com.hotelManagement.system.mapper.AmenityMapper;
import com.hotelManagement.system.repository.AmenityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityResponseDTO create(AmenityCreateDTO dto) {
        if (amenityRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Amenity already exists");
        }

        Amenity entity = AmenityMapper.toEntity(dto);
        return AmenityMapper.toResponse(amenityRepository.save(entity));
    }

    public AmenityResponseDTO update(Integer id, AmenityUpdateDTO dto) {
        Amenity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));

        AmenityMapper.updateEntity(dto, entity);
        return AmenityMapper.toResponse(amenityRepository.save(entity));
    }

    public AmenityResponseDTO get(Integer id) {
        Amenity a = amenityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
        return AmenityMapper.toResponse(a);
    }

    public List<AmenityResponseDTO> getAll() {
        return AmenityMapper.toResponseList(amenityRepository.findAll());
    }

    public void delete(Integer id) {
        amenityRepository.deleteById(id);
    }
}