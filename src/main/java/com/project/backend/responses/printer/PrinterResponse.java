package com.project.backend.responses.printer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Printer;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrinterResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public static PrinterResponse fromPrinter(Printer printer) {
        return PrinterResponse.builder()
                .id(printer.getId())
                .name(printer.getName())
                .build();
    }
}
