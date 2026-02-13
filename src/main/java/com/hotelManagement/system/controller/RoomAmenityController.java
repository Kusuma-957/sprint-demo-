package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.RoomAmenityCreateDTO;
import com.hotelManagement.system.dto.RoomAmenityResponseDTO;
import com.hotelManagement.system.service.RoomAmenityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roomAmenity")
public class RoomAmenityController {

    private final RoomAmenityService service;

    @PostMapping("/post")
    public ResponseEntity<RoomAmenityResponseDTO> create(@Valid @RequestBody RoomAmenityCreateDTO dto) {
        RoomAmenityResponseDTO response = service.createMapping(dto);
        return ResponseEntity.status(201).body(response);
    }
}