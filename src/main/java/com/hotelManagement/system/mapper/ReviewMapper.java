package com.hotelManagement.system.mapper;

import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Review;

import java.time.LocalDate;

public class ReviewMapper {

    public static ReviewResponseDTO toDto(Review entity) {
        if (entity == null) return null;
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setReviewId(entity.getReviewId());
        //dto.setReservationId(entity.getReservation() != null ? getReservationId(entity.getReservation()) : null);
        dto.setReservationId(entity.getReservation() != null
            ? entity.getReservation().getReservationId()
            : null);
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setReviewDate(entity.getReviewDate());
        return dto;
    }

    // Create
    public static Review toEntity(ReviewCreateRequest req, Reservation reservationRef) {
        Review entity = new Review();
        entity.setReservation(reservationRef);
        entity.setRating(req.getRating());
        entity.setComment(req.getComment());
        entity.setReviewDate(req.getReviewDate() != null ? req.getReviewDate() : LocalDate.now());
        return entity;
    }

    // Update in-place (nulls are ignored)
    public static void updateEntity(ReviewUpdateRequest req, Review entity) {
        if (req.getRating() != null) entity.setRating(req.getRating());
        if (req.getComment() != null) entity.setComment(req.getComment());
        if (req.getReviewDate() != null) entity.setReviewDate(req.getReviewDate());
    }
}
