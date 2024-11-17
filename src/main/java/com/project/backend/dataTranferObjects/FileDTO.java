package com.project.backend.dataTranferObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.backend.models.FileFormat;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDTO {
    private Integer id;
    private String size;
    private String name;
    private LocalDateTime uploadDate;
    private String url;
    private FileFormat fileFormat;
}
