//package com.hotelManagement.system.controller;
//
//import com.hotelManagement.system.dto.RoomAmenityCreateDTO;
//import com.hotelManagement.system.dto.RoomAmenityResponseDTO;
//import com.hotelManagement.system.service.RoomAmenityService;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/roomAmenity")
//public class RoomAmenityController {
//
//    private final RoomAmenityService service;
//
//    @PostMapping("/post")
//    public ResponseEntity<RoomAmenityResponseDTO> create(@Valid @RequestBody RoomAmenityCreateDTO dto) {
//        RoomAmenityResponseDTO response = service.createMapping(dto);
//        return ResponseEntity.status(201).body(response);
//    }
//}


package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.RoomAmenityCreateDTO;
import com.hotelManagement.system.dto.RoomAmenityResponseDTO;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
import com.hotelManagement.system.service.RoomAmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roomAmenity")
public class RoomAmenityController {

    private final RoomAmenityService service;

    /**
     * POST /api/roomAmenity/post
     * Success (201):
     * {
     *   "code": "POSTSUCCESS",
     *   "message": "Room amenity added successfully",
     *   "data": { ... }
     * }
     * Duplicate mapping -> 409 with {"code":"ADDFAILS","message":"Room amenity already exist"}
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<RoomAmenityResponseDTO>> create(@Valid @RequestBody RoomAmenityCreateDTO dto) {
        RoomAmenityResponseDTO created = service.createMapping(dto);
        ApiResponse<RoomAmenityResponseDTO> body = ApiResponse.<RoomAmenityResponseDTO>builder()
                .code(ApiCode.POSTSUCCESS)
                .message("Room amenity added successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}