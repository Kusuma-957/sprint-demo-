package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByHotel_HotelId(Integer hotelId);

    @Query("select r from Room r where r.roomType.roomTypeId = :roomTypeId and r.isAvailable = true")
    List<Room> findAvailableRoomsByType(Integer roomTypeId);

    @Query("select r from Room r where r.hotel.location = :location")
    List<Room> findRoomsByLocation(String location);

    @Query("""
        select r from Room r
        join r.amenities a
        where a.amenityId = :amenityId
    """)
    List<Room> findRoomsByAmenity(Integer amenityId);
}