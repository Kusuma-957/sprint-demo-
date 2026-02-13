package com.hotelManagement.system.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ReviewUpdateRequest {
    // All fields optional for PATCH; for PUT we can validate presence in the controller if needed.
    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 2000, message = "comment can be at most 2000 characters")
    private String comment;

    private LocalDate reviewDate;

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
}
