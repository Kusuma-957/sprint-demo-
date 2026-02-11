package com.hotelManagement.system.repository;

import com.hotelManagement.system.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    boolean existsByTypeNameIgnoreCase(String typeName);
}