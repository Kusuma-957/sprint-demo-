package com.hotelManagement.system.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class RoomAmenityRepository {

    @PersistenceContext
    private EntityManager em;

    public boolean existsMapping(Integer roomId, Integer amenityId) {
        String sql = """
            SELECT COUNT(*) FROM RoomAmenity
            WHERE room_id = :roomId AND amenity_id = :amenityId
        """;

        Number result = (Number) em.createNativeQuery(sql)
                .setParameter("roomId", roomId)
                .setParameter("amenityId", amenityId)
                .getSingleResult();

        return result.intValue() > 0;
    }

    public void insertMapping(Integer roomId, Integer amenityId) {
        String sql = """
            INSERT INTO RoomAmenity (room_id, amenity_id)
            VALUES (:roomId, :amenityId)
        """;

        em.createNativeQuery(sql)
                .setParameter("roomId", roomId)
                .setParameter("amenityId", amenityId)
                .executeUpdate();
    }
}