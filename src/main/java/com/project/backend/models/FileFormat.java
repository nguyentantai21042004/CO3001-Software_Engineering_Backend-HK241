package com.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "file_formats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Format name is required")
    private String name;
}
