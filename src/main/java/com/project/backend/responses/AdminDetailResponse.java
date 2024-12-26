package com.project.backend.responses;

import com.project.backend.models.SPSO;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@Builder
public class AdminDetailResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("role")
    private String role;

    @JsonProperty("birthdate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public static AdminDetailResponse fromSPSO(SPSO spso) {
        return AdminDetailResponse.builder()
                .id(spso.getId())
                .name(spso.getName())
                .email(spso.getEmail())
                .phoneNumber(spso.getPhoneNumber())
                .role(spso.getRole().getName())
                .birthdate(spso.getBirthdate())
                .build();
    }
}
