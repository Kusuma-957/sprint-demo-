package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.service.AmenityService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping("/post")
    public ResponseEntity<AmenityResponseDTO> create(@RequestBody AmenityCreateDTO dto) {
        return ResponseEntity.status(201).body(amenityService.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AmenityResponseDTO>> getAll() {
        return ResponseEntity.ok(amenityService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(amenityService.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AmenityResponseDTO> update(
            @PathVariable("id") Integer id,
            @RequestBody AmenityUpdateDTO dto
    ) {
        return ResponseEntity.ok(amenityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        amenityService.delete(id);
        return ResponseEntity.ok().build();
    }
}