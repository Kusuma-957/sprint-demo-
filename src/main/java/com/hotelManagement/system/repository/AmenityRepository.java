package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
	boolean existsByNameIgnoreCase(String name);
}



