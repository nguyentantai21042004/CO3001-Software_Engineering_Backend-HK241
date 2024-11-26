package com.project.backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Location;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public static LocationResponse fromLocation(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .build();
    }
}
