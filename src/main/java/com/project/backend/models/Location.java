package com.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Location name is required")
    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @NotBlank(message = "Campus code is required")
    @Column(name = "campus", length = 3, nullable = false)
    private String campus;

    @NotNull(message = "Floor is required")
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @NotNull(message = "Room number is required")
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

}
