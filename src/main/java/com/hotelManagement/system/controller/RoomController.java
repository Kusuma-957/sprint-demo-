package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    // CSV: POST /api/rooms/post
    @PostMapping("/post")
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomCreateDTO dto) {
        return ResponseEntity.status(201).body(roomService.create(dto));
    }

    // CSV: GET /api/room/all
    @GetMapping("/all")
    public ResponseEntity<List<RoomResponseDTO>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    // CSV: GET /api/room/{roomId}
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> getById(@PathVariable Integer roomId) {
        return ResponseEntity.ok(roomService.getById(roomId));
    }

    // CSV: PUT /api/room/update/{roomId}
    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponseDTO> update(
            @PathVariable Integer roomId,
            @Valid @RequestBody RoomUpdateDTO dto) {
        return ResponseEntity.ok(roomService.update(roomId, dto));
    }

    // CSV: GET /api/rooms/available/{roomTypeId}
    @GetMapping("/available/{roomTypeId}")
    public ResponseEntity<List<RoomResponseDTO>> getAvailable(@PathVariable Integer roomTypeId) {
        return ResponseEntity.ok(roomService.getAvailableByType(roomTypeId));
    }

    // CSV: GET /api/rooms/location/{location}
    @GetMapping("/location/{location}")
    public ResponseEntity<List<RoomResponseDTO>> getByLocation(@PathVariable String location) {
        return ResponseEntity.ok(roomService.getByLocation(location));
    }

    // FIXED: CSV duplicated two endpoints → unified to safe path
    @GetMapping("/by-amenity/{amenityId}")
    public ResponseEntity<List<RoomResponseDTO>> getByAmenity(@PathVariable Integer amenityId) {
        return ResponseEntity.ok(roomService.getByAmenity(amenityId));
    }
}