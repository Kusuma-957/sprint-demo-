//package com.hotelManagement.system.controller;
//
//import com.hotelManagement.system.dto.*;
//import com.hotelManagement.system.service.RoomTypeService;
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
//@RequestMapping("/api/RoomType")
//public class RoomTypeController {
//
//    private final RoomTypeService service;
//
//    @PostMapping("/post")
//    public ResponseEntity<RoomTypeResponseDTO> create(@Valid @RequestBody RoomTypeCreateDTO dto) {
//        return ResponseEntity.status(201).body(service.create(dto));
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<RoomTypeResponseDTO> update(
//            @PathVariable("id") Integer id,
//            @Valid @RequestBody RoomTypeUpdateDTO dto) {
//        return ResponseEntity.ok(service.update(id, dto));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<RoomTypeResponseDTO> get(@PathVariable("id") Integer id) {
//        return ResponseEntity.ok(service.get(id));
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<RoomTypeResponseDTO>> getAll() {
//        return ResponseEntity.ok(service.getAll());
//    }
//}

package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.*;
import com.hotelManagement.system.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.hotelManagement.system.exception.ApiCode;
// If you have these classes, import them. Adjust package if different.
import com.hotelManagement.system.exception.ApiResponse; // {code, message}

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/RoomType")
public class RoomTypeController {

    private final RoomTypeService service;

    // POST /api/RoomType/post
    @PostMapping("/post")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody RoomTypeCreateDTO dto) {
        service.create(dto); // will throw ConflictException if exists
        ApiResponse ok = ApiResponse.builder()
                .code(ApiCode.POSTSUCCESS)
                .message("roomType added successfully")
                .build();
        return ResponseEntity.status(201).body(ok);
    }

    // PUT /api/RoomType/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody RoomTypeUpdateDTO dto) {
        service.update(id, dto); // will throw ResourceNotFoundException if not found
        ApiResponse ok = ApiResponse.builder()
                .code(ApiCode.UPDATESUCCESS)
                .message("RoomType updated successfully")
                .build();
        return ResponseEntity.ok(ok);
    }

    // GET /api/RoomType/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.get(id)); // errors handled globally → GETFAILS
    }

    // GET /api/RoomType/all
    @GetMapping("/all")
    public ResponseEntity<List<RoomTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // DELETE /api/RoomType/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Integer id) {
        service.delete(id); // will throw ResourceNotFoundException if not found
        ApiResponse ok = ApiResponse.builder()
                .code(ApiCode.DELETESUCCESS)
                .message("RoomType deleted successfully")
                .build();
        return ResponseEntity.ok(ok);
    }
}