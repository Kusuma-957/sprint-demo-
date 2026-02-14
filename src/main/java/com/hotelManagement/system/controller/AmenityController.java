//package com.hotelManagement.system.controller;
//
//import com.hotelManagement.system.dto.*;
//import com.hotelManagement.system.exception.ApiCode;
//import com.hotelManagement.system.exception.ApiResponse;
//import com.hotelManagement.system.service.AmenityService;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/amenities")
//public class AmenityController {
//
//    private final AmenityService amenityService;
//
//    @PostMapping("/post")
//    public ResponseEntity<AmenityResponseDTO> create(@RequestBody AmenityCreateDTO dto) {
//        return ResponseEntity.status(201).body(amenityService.create(dto));
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<AmenityResponseDTO>> getAll() {
//        return ResponseEntity.ok(amenityService.getAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<AmenityResponseDTO> get(@PathVariable("id") Integer id) {
//        return ResponseEntity.ok(amenityService.get(id));
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<AmenityResponseDTO> update(
//            @PathVariable("id") Integer id,
//            @RequestBody AmenityUpdateDTO dto
//    ) {
//        return ResponseEntity.ok(amenityService.update(id, dto));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
//    	amenityService.delete(id); // throws ResourceNotFoundException if missing
//        ApiResponse<Void> body = ApiResponse.<Void>builder()
//                .code(ApiCode.DELETESUCCESS)
//                .message("Amenity deleted successfully")
//                .build();
//        return ResponseEntity.ok(body);
//    }
//    
//    @GetMapping("/room/{roomId}")
//    public ResponseEntity<List<AmenityResponseDTO>> getByRoom(@PathVariable("roomId") Integer roomId) {
//        return ResponseEntity.ok(amenityService.getByRoomId(roomId));
//    }  //controller
//    
//    @GetMapping("/by-hotel/{hotelId}")
//    public ResponseEntity<?> getByHotel(@PathVariable("hotelId") Integer hotelId) {
//        return ResponseEntity.ok(amenityService.getByHotelId(hotelId));
//    }
//}

package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.AmenityCreateDTO;
import com.hotelManagement.system.dto.AmenityResponseDTO;
import com.hotelManagement.system.dto.AmenityUpdateDTO;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
import com.hotelManagement.system.exception.ApiResponseData;
import com.hotelManagement.system.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<AmenityResponseDTO>> create(@RequestBody AmenityCreateDTO dto) {
        AmenityResponseDTO created = amenityService.create(dto);
        ApiResponse<AmenityResponseDTO> body = ApiResponse.<AmenityResponseDTO>builder()
                .code(ApiCode.POSTSUCCESS)
                .message("Amenity added successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseData<List<AmenityResponseDTO>>> getAll() {
        List<AmenityResponseDTO> list = amenityService.getAll();
        ApiResponseData<List<AmenityResponseDTO>> body = ApiResponseData.<List<AmenityResponseDTO>>builder()
                .data(list)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseData<AmenityResponseDTO>> get(@PathVariable("id") Integer id) {
        AmenityResponseDTO dto = amenityService.get(id);
        ApiResponseData<AmenityResponseDTO> body = ApiResponseData.<AmenityResponseDTO>builder()
                .data(dto)
                .build();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<AmenityResponseDTO>> update(
            @PathVariable("id") Integer id,
            @RequestBody AmenityUpdateDTO dto) {

        AmenityResponseDTO updated = amenityService.update(id, dto);
        ApiResponse<AmenityResponseDTO> body = ApiResponse.<AmenityResponseDTO>builder()
                .code(ApiCode.UPDATESUCCESS)
                .message("Amenity updated successfully")
                .build();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Integer id) {
        amenityService.delete(id); // throws ResourceNotFoundException if missing
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(ApiCode.DELETESUCCESS)
                .message("Amenity deleted successfully")
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponseData<List<AmenityResponseDTO>>> getByRoom(@PathVariable("roomId") Integer roomId) {
        List<AmenityResponseDTO> list = amenityService.getByRoomId(roomId);
        ApiResponseData<List<AmenityResponseDTO>> body = ApiResponseData.<List<AmenityResponseDTO>>builder()
                .data(list)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/by-hotel/{hotelId}")
    public ResponseEntity<ApiResponseData<List<AmenityResponseDTO>>> getByHotel(@PathVariable("hotelId") Integer hotelId) {
        List<AmenityResponseDTO> list = amenityService.getByHotelId(hotelId);
        ApiResponseData<List<AmenityResponseDTO>> body = ApiResponseData.<List<AmenityResponseDTO>>builder()
                .data(list)
                .build();
        return ResponseEntity.ok(body);
    }
}
