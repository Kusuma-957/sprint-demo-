//package com.hotelManagement.system.controller;
//
//import com.hotelManagement.system.dto.ReviewCreateRequest;
//import com.hotelManagement.system.dto.ReviewResponseDTO;
//import com.hotelManagement.system.dto.ReviewUpdateRequest;
//import com.hotelManagement.system.service.ReviewService;
//import jakarta.validation.Valid;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api")
//@Validated
//public class ReviewController {
//
//    private final ReviewService reviewService; // now concrete class
//
//    public ReviewController(ReviewService reviewService) {
//        this.reviewService = reviewService;
//    }
//
//    @PostMapping("/reviews")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ReviewResponseDTO create(@Valid @RequestBody ReviewCreateRequest request) {
//        return reviewService.create(request);
//    }
//
//    @GetMapping("/reviews/{id}")
//    public ReviewResponseDTO getById(@PathVariable("id") Integer id) {
//        return reviewService.getById(id);
//    }
//
//    @GetMapping("/reviews")
//    public Page<ReviewResponseDTO> list(
//            @PageableDefault(size = 20, sort = "reviewId") Pageable pageable) {
//        return reviewService.list(pageable);
//    }
//
//    @GetMapping("/reservations/{reservationId}/reviews")
//    public Page<ReviewResponseDTO> listByReservation(
//            @PathVariable Integer reservationId,
//            @PageableDefault(size = 20, sort = "reviewDate") Pageable pageable) {
//        return reviewService.listByReservation(reservationId, pageable);
//    }
//
//    @PutMapping("/reviews/{id}")
//    public ReviewResponseDTO putUpdate(
//            @PathVariable("id") Integer id,
//            @Valid @RequestBody ReviewUpdateRequest request) {
//        return reviewService.update(id, request);
//    }
//
//    @PatchMapping("/reviews/{id}")
//    public ReviewResponseDTO patchUpdate(
//            @PathVariable("id") Integer id,
//            @RequestBody ReviewUpdateRequest request) {
//        return reviewService.update(id, request);
//    }
//
//    @DeleteMapping("/reviews/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable("id") Integer id) {
//        reviewService.delete(id);
//    }
//}



package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.HotelResponseDTO;
import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.exception.ApiCode;
import com.hotelManagement.system.exception.ApiResponse;
import com.hotelManagement.system.service.ReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review/post")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> create(@RequestBody ReviewCreateRequest req) {
        reviewService.create(req);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.POSTSUCCESS)
                .message("Hotel added successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
        //return ResponseEntity.ok(new ApiMessage("POSTSUCCESS", "Review added successfully"));
    }

    @GetMapping("/review/all")
    public List<ReviewResponseDTO> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/review/{review_id}")
    public ReviewResponseDTO getById(@PathVariable("review_id") Integer id) {
        return reviewService.getById(id);
    }

    @GetMapping("/reviews/rating/{rating}")
    public List<ReviewResponseDTO> getByRating(@PathVariable("rating") Integer rating) {
        return reviewService.getByRating(rating);
    }

    @GetMapping("/reviews/recent")
    public List<ReviewResponseDTO> getRecent() {
        return reviewService.getRecent();
    }

    @PutMapping("/review/update/{review_id}")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> update(@PathVariable("review_id") Integer id,
                                             @RequestBody ReviewUpdateRequest req) {
        reviewService.update(id, req);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.UPDATESUCCESS)
                .message("Review updated successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
        //return ResponseEntity.ok(new ApiMessage("UPDATESUCCESS", "Review updated successfully"));
    }

    @DeleteMapping("/review/delete/{review_id}")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> delete(@PathVariable("review_id") Integer id) {
        reviewService.delete(id);
        ApiResponse<HotelResponseDTO> body = ApiResponse.<HotelResponseDTO>builder()
                .code(ApiCode.DELETESUCCESS)
                .message("Review deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}