package com.hotelManagement.system.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roomtype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
