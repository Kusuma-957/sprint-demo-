package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/RoomType")
public class RoomTypeController {

    private final RoomTypeService service;

    @PostMapping("/post")
    public ResponseEntity<RoomTypeResponseDTO> create(@Valid @RequestBody RoomTypeCreateDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RoomTypeResponseDTO> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody RoomTypeUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}