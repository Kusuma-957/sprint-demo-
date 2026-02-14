//package com.hotelManagement.system.repository;
//
//import com.hotelManagement.system.entity.Reservation;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
//
//    // Overlap: (existing.checkIn < new.checkOut) AND (existing.checkOut > new.checkIn)
//    @Query("""
//        select case when count(res) > 0 then true else false end
//        from Reservation res
//        where res.room.roomId = :roomId
//          and res.checkInDate < :checkOut
//          and res.checkOutDate > :checkIn
//    """)
//    boolean existsOverlap(
//            @Param("roomId") Integer roomId,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut
//    );
//
//    // Overlap when updating (exclude current reservation)
//    @Query("""
//        select case when count(res) > 0 then true else false end
//        from Reservation res
//        where res.room.roomId = :roomId
//          and res.reservationId <> :reservationId
//          and res.checkInDate < :checkOut
//          and res.checkOutDate > :checkIn
//    """)
//    boolean existsOverlapExcluding(
//            @Param("reservationId") Integer reservationId,
//            @Param("roomId") Integer roomId,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut
//    );
//
//    @Query("""
//        select res from Reservation res
//        where res.checkInDate <= :end
//          and res.checkOutDate >= :start
//        order by res.checkInDate asc
//    """)
//    List<Reservation> findWithinDateRange(
//            @Param("start") LocalDate start,
//            @Param("end") LocalDate end
//    );
//}


package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    // Overlap: (existing.checkIn < new.checkOut) AND (existing.checkOut > new.checkIn)
    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.room.roomId = :roomId
          and res.checkInDate < :checkOut
          and res.checkOutDate > :checkIn
    """)
    boolean existsOverlap(
            @Param("roomId") Integer roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Overlap when updating (exclude current reservation)
    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.room.roomId = :roomId
          and res.reservationId <> :reservationId
          and res.checkInDate < :checkOut
          and res.checkOutDate > :checkIn
    """)
    boolean existsOverlapExcluding(
            @Param("reservationId") Integer reservationId,
            @Param("roomId") Integer roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // ✅ Exact duplicate for CREATE
    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.room.roomId = :roomId
          and res.checkInDate = :checkIn
          and res.checkOutDate = :checkOut
    """)
    boolean existsExactDuplicate(
            @Param("roomId") Integer roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // ✅ Exact duplicate for UPDATE (exclude current)
    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.reservationId <> :reservationId
          and res.room.roomId = :roomId
          and res.checkInDate = :checkIn
          and res.checkOutDate = :checkOut
    """)
    boolean existsExactDuplicateExcluding(
            @Param("reservationId") Integer reservationId,
            @Param("roomId") Integer roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("""
        select res from Reservation res
        where res.checkInDate <= :end
          and res.checkOutDate >= :start
        order by res.checkInDate asc
    """)
    List<Reservation> findWithinDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // (From earlier message) Optional helpers if you’re flipping isAvailable on delete
    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.room.roomId = :roomId
          and res.checkOutDate > :today
    """)
    boolean existsAnyActiveOrFutureForRoom(@Param("roomId") Integer roomId,
                                           @Param("today") LocalDate today);

    @Query("""
        select case when count(res) > 0 then true else false end
        from Reservation res
        where res.room.roomId = :roomId
          and res.reservationId <> :excludeReservationId
          and res.checkOutDate > :today
    """)
    boolean existsAnyActiveOrFutureForRoomExcluding(@Param("roomId") Integer roomId,
                                                    @Param("excludeReservationId") Integer excludeReservationId,
                                                    @Param("today") LocalDate today);
}
