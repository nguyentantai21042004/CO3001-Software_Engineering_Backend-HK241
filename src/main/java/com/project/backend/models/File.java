package com.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String size;
    @NotBlank(message = "File name is required")
    private String name;
    private LocalDateTime uploadDate;
    private String url;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "format_id")
    private FileFormat fileFormat;
}
