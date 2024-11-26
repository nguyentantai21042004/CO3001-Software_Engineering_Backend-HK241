package com.project.backend.responses.students;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Student;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponse {
    @JsonProperty("id")
    private Integer studentId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("student_balance")
    private Integer studentBalance;

    @JsonProperty("join_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime joinDate;

    @JsonProperty("last_login")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    public static StudentDetailResponse fromStudent(Student student) {
        return StudentDetailResponse.builder()
                .studentId(student.getStudentId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .imageUrl(student.getImageUrl())
                .studentBalance(student.getStudentBalance())
                .joinDate(student.getJoinDate())
                .lastLogin(student.getLastLogin())
                .build();
    }
}
