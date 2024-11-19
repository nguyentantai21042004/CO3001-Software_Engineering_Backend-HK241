package com.project.backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.File;
import com.project.backend.models.FileFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("size")
    private String size;
    @JsonProperty("name")
    private String name;
    @JsonProperty("upload_date")
    private LocalDateTime uploadDate;
    @JsonProperty("url")
    private String url;
    @JsonProperty("file_format")
    private String fileFormat;

    public static FileResponse fromFile(File file){
        return  FileResponse.builder()
                .id(file.getId())
                .size(file.getSize())
                .name(file.getName())
                .uploadDate(file.getUploadDate())
                .url(file.getUrl())
                .fileFormat(file.getFileFormat().getName())
                .build();
    }
}
