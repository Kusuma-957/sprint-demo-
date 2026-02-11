package com.hotelManagement.system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponseDTO {
    private Integer amenityId;
    private String name;
    private String description;
}