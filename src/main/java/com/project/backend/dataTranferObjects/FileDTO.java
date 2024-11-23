package com.project.backend.dataTranferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.FileFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDTO {
    @JsonProperty("size")
    private String size;
    @JsonProperty("name")
    private String name;
    @JsonProperty("upload_date")
    private LocalDateTime uploadDate;
    @JsonProperty("url")
    private String url;
    @JsonProperty("file_format")
    private FileFormat fileFormat;
}
