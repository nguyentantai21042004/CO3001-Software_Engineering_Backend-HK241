package com.project.backend.controllers;

import com.project.backend.models.Printer;
import com.project.backend.models.SPSO;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.printer.PrinterService;
import com.project.backend.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/printers")
@RequiredArgsConstructor
public class PrinterController {

        private final PrinterService printerService;
        private final AdminService adminService; // To fetch SPSO details from the token

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
        public ResponseEntity<ResponseObject> addPrinter(@RequestBody Printer printer,
                                                         @RequestHeader("Authorization") String authorizationHeader) throws Exception {
            // Extract token from the Authorization header
            String token = authorizationHeader.substring(7);

            // Fetch SPSO from token and set it in Printer
            SPSO spso = adminService.getUserDetailsFromToken(token);
            printer.setSpso(spso);

            ResponseObject response = printerService.addPrinter(printer);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> updatePrinter(
            @PathVariable Integer id,
            @RequestBody Printer updatedPrinter,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        // Extract token from the Authorization header
        String token = authorizationHeader.substring(7);

        // Fetch SPSO from token
        SPSO tokenSpso = adminService.getUserDetailsFromToken(token);

        if (tokenSpso == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .message("Unauthorized: SPSO from token does not exist.")
                    .status(HttpStatus.FORBIDDEN)
                    .data(null)
                    .build());
        }

        // Check if the user has the ADMIN role
        boolean isAdmin = "ADMIN".equals(tokenSpso.getRole().getName());

        // If the user is an ADMIN and the request body contains an SPSO, validate and fetch it
        if (isAdmin && updatedPrinter.getSpso() != null) {
            SPSO requestBodySpso = updatedPrinter.getSpso();

            // If the SPSO ID is null, use the SPSO from the token
            if (requestBodySpso.getId() == null) {
                updatedPrinter.setSpso(tokenSpso);
                // Proceed with the update
                ResponseObject response = printerService.updatePrinter(id, updatedPrinter);
                return ResponseEntity.status(response.getStatus()).body(response);
            }

            // Fetch SPSO from the database using its ID
            SPSO validSpso = adminService.findSpsoById(Long.valueOf(requestBodySpso.getId()));
            if (validSpso == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                        .message("SPSO with ID " + requestBodySpso.getId() + " does not exist.")
                        .status(HttpStatus.NOT_FOUND)
                        .data(null)
                        .build());
            }

            updatedPrinter.setSpso(validSpso);
        } else {
            // If not ADMIN or no SPSO in the request body, use the SPSO from the token
            updatedPrinter.setSpso(tokenSpso);
        }

        // Proceed with the update
        ResponseObject response = printerService.updatePrinter(id, updatedPrinter);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
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

        @GetMapping("/spso")
        @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
        public ResponseEntity<ResponseObject> getPrintersBySpsoUsingToken(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
            // Extract token from the Authorization header
            String token = authorizationHeader.substring(7);

            // Fetch SPSO from token
            SPSO spso = adminService.getUserDetailsFromToken(token);

            // Fetch printers by SPSO ID
            ResponseObject response = printerService.findBySpsoId(spso.getId());
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        @GetMapping("/spso/{spsoId}")
        @PreAuthorize("hasAnyRole('ADMIN')")
        public ResponseEntity<ResponseObject> getPrintersBySpsoId(@PathVariable Integer spsoId) {
            ResponseObject response = printerService.findBySpsoId(spsoId);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
}
