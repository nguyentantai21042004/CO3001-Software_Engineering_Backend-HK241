package com.project.backend.controllers;

import com.project.backend.models.Printer;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.printer.PrinterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/printers")
@RequiredArgsConstructor
public class PrinterController {

    private final PrinterService printerService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
    public ResponseEntity<ResponseObject> getAllPrinters() {
        ResponseObject response = printerService.findAllPrinters();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
    public ResponseEntity<ResponseObject> getPrinterById(@PathVariable Integer id) {
        ResponseObject response = printerService.findPrinterById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> addPrinter(@RequestBody Printer printer) {
        ResponseObject response = printerService.addPrinter(printer);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> updatePrinter(
            @PathVariable Integer id,
            @RequestBody Printer updatedPrinter) {
        ResponseObject response = printerService.updatePrinter(id, updatedPrinter);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<ResponseObject> deletePrinter(@PathVariable Integer id) {
        ResponseObject response = printerService.deletePrinter(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
    public ResponseEntity<ResponseObject> getPrintersByLocation(@PathVariable Integer locationId) {
        ResponseObject response = printerService.findByLocationId(locationId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/spso/{spsoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> getPrintersBySpsoId(@PathVariable Integer spsoId) {
        ResponseObject response = printerService.findBySpsoId(spsoId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
