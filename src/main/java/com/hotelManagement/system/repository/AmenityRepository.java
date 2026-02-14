package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Amenity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
	boolean existsByNameIgnoreCase(String name);
	@Query(value = """
	        SELECT a.*
	        FROM Amenity a
	        JOIN RoomAmenity ra ON a.amenity_id = ra.amenity_id
	        WHERE ra.room_id = :roomId
	    """, nativeQuery = true)
	    List<Amenity> findByRoomId(@Param("roomId") Integer roomId);      //repository
	@Query(value = """
		    SELECT a.* FROM Amenity a
		    JOIN HotelAmenity ha ON a.amenity_id = ha.amenity_id
		    WHERE ha.hotel_id = :hotelId
		""", nativeQuery = true)
		List<Amenity> findByHotelId(@Param("hotelId") Integer hotelId);
}



