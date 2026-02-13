package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Using native query because we know the exact join column name: reservation_id
    @Query(
        value = "SELECT * FROM Review WHERE reservation_id = :reservationId",
        countQuery = "SELECT COUNT(*) FROM Review WHERE reservation_id = :reservationId",
        nativeQuery = true
    )
    Page<Review> findByReservationId(@Param("reservationId") Integer reservationId, Pageable pageable);
}