package com.project.backend.responses.printer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Printer;
import com.project.backend.responses.LocationResponse;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrinterDetailResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("location")
    private LocationResponse location;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    public static PrinterDetailResponse fromPrinter(Printer printer) {
        return PrinterDetailResponse.builder()
                .id(printer.getId())
                .name(printer.getName())
                .location(LocationResponse.fromLocation(printer.getLocation()))
                .description(printer.getDescription())
                .status(printer.getStatus().toString())
                .build();
    }
}
