package com.project.backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.FileFormat;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileFormatResponse {
    @JsonProperty("format_id")
    private Integer formatId;
    @JsonProperty("name")
    private String name;

    public static FileFormatResponse fromFileFormat(FileFormat fileFormat){
        return FileFormatResponse.builder()
                .formatId(fileFormat.getId())
                .name(fileFormat.getName())
                .build();
    }
}
