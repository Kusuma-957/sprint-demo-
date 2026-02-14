//package com.hotelManagement.system.controller;
// 
// 
//import com.hotelManagement.system.dto.HotelCreateDTO;
//import com.hotelManagement.system.dto.HotelResponseDTO;
//import com.hotelManagement.system.dto.HotelUpdateDTO;
//import com.hotelManagement.system.service.HotelService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
// 
//import java.util.List;
// 
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/hotels")
//public class HotelController {
// 
//    private final HotelService hotelService;
//    @PostMapping("/post")
//    public ResponseEntity<HotelResponseDTO> create(@Valid @RequestBody HotelCreateDTO dto) {
//        HotelResponseDTO created = hotelService.create(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
// 
//    @GetMapping("/all")
//    public ResponseEntity<List<HotelResponseDTO>> getAll() {
//        return ResponseEntity.ok(hotelService.getAll());
//    }
// 
//    @GetMapping("/{hotelId}")
//    public ResponseEntity<HotelResponseDTO> getById(@PathVariable("hotelId") Integer hotelId) {
//        return ResponseEntity.ok(hotelService.getById(hotelId));
//    }
// 
//    @GetMapping("/by-amenity/{amenityId}")
//    public ResponseEntity<List<HotelResponseDTO>> getByAmenity(@PathVariable("amenityId") Integer amenityId) {
//        return ResponseEntity.ok(hotelService.getByAmenity(amenityId));
//    }
// 
//    @PutMapping("/update/{hotelId}")
//    public ResponseEntity<HotelResponseDTO> update(@PathVariable("hotelId") Integer hotelId,
//                                                   @Valid @RequestBody HotelUpdateDTO dto) {
//        return ResponseEntity.ok(hotelService.update(hotelId, dto));
//    }
//}
package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.HotelCreateDTO;
import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.HotelUpdateDTO;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
import com.hotelManagement.system.exception.ApiResponseData;
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

    /**
     * Create a new Hotel
     * Success (201): {"code": "POSTSUCCESS", "message": "Hotel added successfully", "data": {...}}
     * Errors handled by GlobalExceptionHandler (e.g., ADDFAILS on duplicates)
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> create(@Valid @RequestBody HotelCreateDTO dto) {
        HotelResponseDTO created = hotelService.create(dto);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.POSTSUCCESS)
                .message("Hotel added successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Get a list of all hotels
     * If empty, service throws EmptyListException -> {"code":"GETALLFAILS","message":"Hotel list is empty"}
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseData<List<HotelResponseDTO>>> getAll() {
        List<HotelResponseDTO> hotels = hotelService.getAll();
        ApiResponseData<List<HotelResponseDTO>> body = ApiResponseData.<List<HotelResponseDTO>>builder()
                .data(hotels)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Get hotel by id
     * If not found -> {"code":"GETFAILS","message":"Hotel not found: {id}"}
     */
    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponseData<HotelResponseDTO>> getById(@PathVariable("hotelId") Integer hotelId) {
        HotelResponseDTO dto = hotelService.getById(hotelId);
        ApiResponseData<HotelResponseDTO> body = ApiResponseData.<HotelResponseDTO>builder()
                .data(dto)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Get hotels by amenity id (kept /by-amenity to avoid path collision with /{hotelId})
     * If amenity doesn't exist -> GETFAILS (404)
     * If amenity exists but no hotels -> GETALLFAILS (404) with a descriptive message
     */
    @GetMapping("/by-amenity/{amenityId}")
    public ResponseEntity<ApiResponseData<List<HotelResponseDTO>>> getByAmenity(@PathVariable("amenityId") Integer amenityId) {
        List<HotelResponseDTO> hotels = hotelService.getByAmenity(amenityId);
        ApiResponseData<List<HotelResponseDTO>> body = ApiResponseData.<List<HotelResponseDTO>>builder()
                .data(hotels)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Update hotel details
     * Success (200): {"code":"UPDATESUCCESS","message":"Hotel updated successfully","data":{...}}
     * Not found -> UPDTFAILS (404) via advice
     * Duplicate name/location -> ADDFAILS (409) via advice
     */
    @PutMapping("/update/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> update(@PathVariable("hotelId") Integer hotelId,
                                                                @Valid @RequestBody HotelUpdateDTO dto) {
        HotelResponseDTO updated = hotelService.update(hotelId, dto);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.UPDATESUCCESS)
                .message("Hotel updated successfully")
                .build();
        return ResponseEntity.ok(body);
    }
    
    
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> delete(@PathVariable("hotelId") Integer hotelId) {
        hotelService.delete(hotelId);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.DELETESUCCESS)
                .message("Hotel deleted successfully")
                .build();
        return ResponseEntity.ok(body);
    }
}