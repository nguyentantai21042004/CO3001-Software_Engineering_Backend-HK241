package com.project.backend.dataTranferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileFormatDTO {
    @JsonProperty("name")
    private String name;
}
