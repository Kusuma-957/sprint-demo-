package com.hotelManagement.system.dto;

import java.time.LocalDate;

public class ReviewResponseDTO {
    private Integer reviewId;
    private Integer reservationId;
    private Integer rating;
    private String comment;
    private LocalDate reviewDate;

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
}
