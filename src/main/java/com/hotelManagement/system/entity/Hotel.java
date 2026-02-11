//package com.hotelManagement.system.entity;
//
//import java.util.Set;
//import java.util.HashSet;
//
//import jakarta.persistence.Id;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinTable;
//import jakarta.persistence.Lob;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "Hotel")
//public class Hotel {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "hotel_id", nullable = false)
//	private Integer hotelId;
//
//    private String name;
//
//    private String location;
//
//    @Lob
//    private String description;
//
//    @ManyToMany
//    @JoinTable(
//        name = "HotelAmenity",
//        joinColumns = @JoinColumn(name = "hotel_id"),
//        inverseJoinColumns = @JoinColumn(name = "amenity_id")
//    )
//    private Set<Amenity> amenities = new HashSet<>();
//
//
//    // Getters & setters omitted for brevity
//}

package com.hotelManagement.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(
        name = "HotelAmenity",
        joinColumns = @JoinColumn(name = "hotel_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
}
