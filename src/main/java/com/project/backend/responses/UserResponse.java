package com.project.backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.SPSO;
import com.project.backend.models.Student;

import lombok.*;

@Data // toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private String role;

    public static UserResponse fromSPSO(SPSO spso) {
        return UserResponse.builder()
                .userId(spso.getId().longValue())
                .email(spso.getEmail())
                .username(spso.getUsername())
                .role(spso.getRole().getName())
                .build();
    }

    public static UserResponse fromStudent(Student student) {
        return UserResponse.builder()
                .userId(student.getStudentId().longValue())
                .email(student.getEmail())
                .username(student.getUsername())
                .role(student.getRole().getName())
                .build();
    }
}
