package com.project.backend.dataTranferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("role")
    private String role;
}
