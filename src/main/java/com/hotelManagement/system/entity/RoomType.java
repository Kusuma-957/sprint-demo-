package com.hotelManagement.system.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "RoomType")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;

    private String typeName;

    @Lob
    private String description;

    private Integer maxOccupancy;

    private BigDecimal pricePerNight;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    // Getters & setters
}
