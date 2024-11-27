package com.project.backend.dataTranferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrintJobCreateDTO {
    @JsonProperty("file_id")
    private Integer fileId;

    @JsonProperty("printer_id")
    private Long printerId;

    @JsonProperty("page_number")
    private String pageNumber;
    
    @JsonProperty("page_size")
    private String pageSize;

    @JsonProperty("number_of_copies")
    private Integer numberOfCopies;

    @JsonProperty("color_mode")
    private Boolean colorMode;

    @JsonProperty("page_side")
    private String pageSide;

    @JsonProperty("is_duplex")
    private Boolean isDuplex;
}
