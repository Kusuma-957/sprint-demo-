package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Review;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    /**
     * Duplicate check: same (reservationId, comment (case-insensitive), rating, reviewDate).
     * We LOWER(CAST(comment AS string)) on entity side and compare with already-lowercased param.
     */
    @Query("""
           SELECT (COUNT(r) > 0)
             FROM Review r
            WHERE r.reservation.reservationId = :reservationId
              AND LOWER(CAST(r.comment AS string)) = :normalizedCommentLower
              AND r.rating = :rating
              AND r.reviewDate = :reviewDate
           """)
    boolean existsDuplicate(@Param("reservationId") Integer reservationId,
                            @Param("normalizedCommentLower") String normalizedCommentLower,
                            @Param("rating") Integer rating,
                            @Param("reviewDate") LocalDate reviewDate);

    // Rating filter
    List<Review> findByRating(Integer rating);

    // Recent N by reviewDate desc (adjust field if you use createdAt instead)
    List<Review> findTop5ByOrderByReviewDateDesc();
}