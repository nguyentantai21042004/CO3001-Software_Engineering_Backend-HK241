package com.project.backend.responses.students;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Student;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponse {
    @JsonProperty("student_id")
    private Long studentId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image_url")
    private String imageUrl;

    public static StudentResponse fromStudent(Student student) {
        return StudentResponse.builder()
                .studentId(Long.valueOf(student.getStudentId()))
                .fullName(student.getFullName())
                .email(student.getEmail())
                .imageUrl(student.getImageUrl())
                .build();
    }
}
