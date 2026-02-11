package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
  boolean existsByNameIgnoreCaseAndLocationIgnoreCase(String name, String location);
  // Distinct hotels that have a link in HotelAmenity to the given amenity id
    @Query(
        value = """
            SELECT h.*
            FROM Hotel h
            JOIN HotelAmenity ha ON h.hotel_id = ha.hotel_id
            WHERE ha.amenity_id = :amenityId
        """,
        nativeQuery = true
    )
    List<Hotel> findHotelsByAmenity(@Param("amenityId") Integer amenityId);
}



