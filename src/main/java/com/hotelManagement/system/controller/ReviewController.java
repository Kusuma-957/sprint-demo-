package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewService reviewService; // now concrete class

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDTO create(@Valid @RequestBody ReviewCreateRequest request) {
        return reviewService.create(request);
    }

    @GetMapping("/reviews/{id}")
    public ReviewResponseDTO getById(@PathVariable("id") Integer id) {
        return reviewService.getById(id);
    }

    @GetMapping("/reviews")
    public Page<ReviewResponseDTO> list(
            @PageableDefault(size = 20, sort = "reviewId") Pageable pageable) {
        return reviewService.list(pageable);
    }

    @GetMapping("/reservations/{reservationId}/reviews")
    public Page<ReviewResponseDTO> listByReservation(
            @PathVariable Integer reservationId,
            @PageableDefault(size = 20, sort = "reviewDate") Pageable pageable) {
        return reviewService.listByReservation(reservationId, pageable);
    }

    @PutMapping("/reviews/{id}")
    public ReviewResponseDTO putUpdate(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return reviewService.update(id, request);
    }

    @PatchMapping("/reviews/{id}")
    public ReviewResponseDTO patchUpdate(
            @PathVariable("id") Integer id,
            @RequestBody ReviewUpdateRequest request) {
        return reviewService.update(id, request);
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        reviewService.delete(id);
    }
}