package com.project.backend.responses.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.File;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public static FileResponse fromFile(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .build();
    }
}
