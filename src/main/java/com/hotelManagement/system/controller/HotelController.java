package com.hotelManagement.system.controller;


import com.hotelManagement.system.dto.HotelCreateDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    @PostMapping("/post")
    public ResponseEntity<HotelResponseDTO> create(@Valid @RequestBody HotelCreateDTO dto) {
        HotelResponseDTO created = hotelService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HotelResponseDTO>> getAll() {
        return ResponseEntity.ok(hotelService.getAll());
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDTO> getById(@PathVariable("hotelId") Integer hotelId) {
        return ResponseEntity.ok(hotelService.getById(hotelId));
    }

    @GetMapping("/by-amenity/{amenityId}")
    public ResponseEntity<List<HotelResponseDTO>> getByAmenity(@PathVariable("amenityId") Integer amenityId) {
        return ResponseEntity.ok(hotelService.getByAmenity(amenityId));
    }

    @PutMapping("/update/{hotelId}")
    public ResponseEntity<HotelResponseDTO> update(@PathVariable("hotelId") Integer hotelId,
                                                   @Valid @RequestBody HotelUpdateDTO dto) {
        return ResponseEntity.ok(hotelService.update(hotelId, dto));
    }
}