//package com.hotelManagement.system.controller;
//
//import com.hotelManagement.system.dto.RoomCreateDTO;
//import com.hotelManagement.system.dto.RoomResponseDTO;
//import com.hotelManagement.system.dto.RoomUpdateDTO;
//import com.hotelManagement.system.service.RoomService;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/rooms")
//public class RoomController {
//
//    private final RoomService roomService;
//
//    // CSV: POST /api/rooms/post
//    @PostMapping("/post")
//    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomCreateDTO dto) {
//        return ResponseEntity.status(201).body(roomService.create(dto));
//    }
//
//    // CSV: GET /api/room/all
//    @GetMapping("/all")
//    public ResponseEntity<List<RoomResponseDTO>> getAll() {
//        return ResponseEntity.ok(roomService.getAll());
//    }
//
//    // CSV: GET /api/room/{roomId}
//    @GetMapping("/{roomId}")
//    public ResponseEntity<RoomResponseDTO> getById(@PathVariable("roomId") Integer roomId) {
//        return ResponseEntity.ok(roomService.getById(roomId));
//    }
//
//    // CSV: PUT /api/room/update/{roomId}
//    @PutMapping("/update/{roomId}")
//    public ResponseEntity<RoomResponseDTO> update(
//            @PathVariable("roomId") Integer roomId,
//            @Valid @RequestBody RoomUpdateDTO dto) {
//        return ResponseEntity.ok(roomService.update(roomId, dto));
//    }
//
//    // CSV: GET /api/rooms/available/{roomTypeId}
//    @GetMapping("/available/{roomTypeId}")
//    public ResponseEntity<List<RoomResponseDTO>> getAvailable(@PathVariable("roomTypeId") Integer roomTypeId) {
//        return ResponseEntity.ok(roomService.getAvailableByType(roomTypeId));
//    }
//
//    // CSV: GET /api/rooms/location/{location}
////    @GetMapping("/location/{location}")
////    public ResponseEntity<List<RoomResponseDTO>> getByLocation(@PathVariable String location) {
////        return ResponseEntity.ok(roomService.getByLocation(location));
////    }
//
//    // FIXED: CSV duplicated two endpoints → unified to safe path
//    @GetMapping("/by-amenity/{amenityId}")
//    public ResponseEntity<List<RoomResponseDTO>> getByAmenity(@PathVariable("amenityId") Integer amenityId) {
//        return ResponseEntity.ok(roomService.getByAmenity(amenityId));
//    }
//}



package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.RoomCreateDTO;
import com.hotelManagement.system.dto.RoomResponseDTO;
import com.hotelManagement.system.dto.RoomUpdateDTO;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
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

    // POST
    @PostMapping("/post")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody RoomCreateDTO dto) {
        roomService.create(dto);

        ApiResponse resp = ApiResponse.builder()
                .code(ApiCode.POSTSUCCESS)
                .message("room added successfully")
                .build();

        return ResponseEntity.status(201).body(resp);
    }

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<RoomResponseDTO>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    // GET BY ID
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> getById(@PathVariable("roomId") Integer roomId) {
        return ResponseEntity.ok(roomService.getById(roomId));
    }

    // UPDATE
    @PutMapping("/update/{roomId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable("roomId") Integer roomId,
            @Valid @RequestBody RoomUpdateDTO dto) {

        roomService.update(roomId, dto);

        ApiResponse resp = ApiResponse.builder()
                .code(ApiCode.UPDATESUCCESS)
                .message("Room updated successfully")
                .build();

        return ResponseEntity.ok(resp);
    }

    // GET AVAILABLE ROOMS BY TYPE
    @GetMapping("/available/{roomTypeId}")
    public ResponseEntity<List<RoomResponseDTO>> getAvailable(
            @PathVariable("roomTypeId") Integer roomTypeId) {
        return ResponseEntity.ok(roomService.getAvailableByType(roomTypeId));
    }

    // GET ROOMS BY AMENITY
    @GetMapping("/by-amenity/{amenityId}")
    public ResponseEntity<List<RoomResponseDTO>> getByAmenity(
            @PathVariable("amenityId") Integer amenityId) {
        return ResponseEntity.ok(roomService.getByAmenity(amenityId));
    }

    // DELETE
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("roomId") Integer roomId) {

        roomService.delete(roomId);

        ApiResponse resp = ApiResponse.builder()
                .code(ApiCode.DELETESUCCESS)
                .message("room deleted successfully")
                .build();

        return ResponseEntity.ok(resp);
    }
}
