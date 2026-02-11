package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.HotelAmenityCreateDTO;
import com.hotelManagement.system.dto.HotelAmenityResponseDTO;
import com.hotelManagement.system.service.HotelAmenityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotelamenity")
public class HotelAmenityController {

    private final HotelAmenityService service;

    // ONLY ONE ENDPOINT AS PER YOUR CSV
    @PostMapping("/post")
    public ResponseEntity<HotelAmenityResponseDTO> create(@Valid @RequestBody HotelAmenityCreateDTO dto) {
        HotelAmenityResponseDTO response = service.createMapping(dto);
        return ResponseEntity.status(201).body(response);
    }
}