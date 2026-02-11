package com.hotelManagement.system.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class HotelAmenityRepository {

    @PersistenceContext
    private EntityManager em;

    public void insertMapping(Integer hotelId, Integer amenityId) {
        String sql = """
            INSERT INTO HotelAmenity (hotel_id, amenity_id)
            VALUES (:hotelId, :amenityId)
        """;

        em.createNativeQuery(sql)
                .setParameter("hotelId", hotelId)
                .setParameter("amenityId", amenityId)
                .executeUpdate();
    }

    public boolean existsMapping(Integer hotelId, Integer amenityId) {
        String sql = """
            SELECT COUNT(*) FROM HotelAmenity
            WHERE hotel_id = :hotelId AND amenity_id = :amenityId
        """;

        Number result = (Number) em.createNativeQuery(sql)
                .setParameter("hotelId", hotelId)
                .setParameter("amenityId", amenityId)
                .getSingleResult();

        return result.intValue() > 0;
    }
}