package com.project.backend.services.printer;

import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.models.Printer;
import com.project.backend.repositories.PrinterRepository;
import com.project.backend.responses.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrinterService implements IPrinterService {

    private final PrinterRepository printerRepository;

    @Override
    public ResponseObject findAllPrinters() {
        ResponseObject response = new ResponseObject();
        try {
            List<Printer> printers = printerRepository.findAll();
            response.setData(printers);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched all printers successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Printer Detail(Integer id) throws Exception {
        Optional<Printer> printer = printerRepository.findById(id);
        if (printer.isEmpty()) {
            throw new DataNotFoundException("Printer not found");
        }
        return printer.get();
    }

    @Override
    public ResponseObject findPrinterById(Integer id) {
        ResponseObject response = new ResponseObject();
        try {
            Optional<Printer> printer = printerRepository.findById(id);
            if (printer.isEmpty()) {
                throw new DataNotFoundException("Printer not found");
            }
            response.setData(printer.get());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Printer found successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject addPrinter(Printer printer) {
        ResponseObject response = new ResponseObject();
        try {
            // Validate a4_remaining_pages and a3_remaining_pages
            if (printer.getA4RemainingPages() < 0 || printer.getA3RemainingPages() < 0) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Remaining pages cannot be negative.");
                return response;
            }

            // Save printer to repository
            printerRepository.save(printer);
            response.setStatus(HttpStatus.CREATED);
            response.setMessage("Printer added successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject updatePrinter(Integer id, Printer updatedPrinter) {
        ResponseObject response = new ResponseObject();
        try {
            // Validate a4_remaining_pages and a3_remaining_pages
            if (updatedPrinter.getA4RemainingPages() < 0 || updatedPrinter.getA3RemainingPages() < 0) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Remaining pages cannot be negative.");
                return response;
            }

            // Check if the printer exists
            Optional<Printer> existingPrinter = printerRepository.findById(id);
            if (existingPrinter.isEmpty()) {
                throw new DataNotFoundException("Printer not found");
            }

            // Update the printer
            Printer printer = existingPrinter.get();
            printer.setName(updatedPrinter.getName());
            printer.setBrand(updatedPrinter.getBrand());
            printer.setType(updatedPrinter.getType());
            printer.setDescription(updatedPrinter.getDescription());
            printer.setSupportContact(updatedPrinter.getSupportContact());
            printer.setLastMaintenanceDate(LocalDateTime.now());
            printer.setStatus(updatedPrinter.getStatus());
            printer.setA4RemainingPages(updatedPrinter.getA4RemainingPages());
            printer.setA3RemainingPages(updatedPrinter.getA3RemainingPages());
            printer.setLocation(updatedPrinter.getLocation());
            printer.setSpso(updatedPrinter.getSpso());

            // Save the updated printer
            printerRepository.save(printer);

            response.setStatus(HttpStatus.OK);
            response.setMessage("Printer updated successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject deletePrinter(Integer id) {
        ResponseObject response = new ResponseObject();
        try {
            if (!printerRepository.existsById(id)) {
                throw new DataNotFoundException("Printer not found");
            }
            printerRepository.deleteById(id);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Printer deleted successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Find printers by location
    @Override
    public ResponseObject findByLocationId(Integer locationId) {
        ResponseObject response = new ResponseObject();
        try {
            List<Printer> printers = printerRepository.findByLocationId(locationId);
            response.setData(printers);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched printers by location successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Find printers by SPSO ID
    @Override
    public ResponseObject findBySpsoId(Integer spsoId) {
        ResponseObject response = new ResponseObject();
        try {
            List<Printer> printers = printerRepository.findBySpsoId(spsoId);
            response.setData(printers);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched printers by SPSO ID successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
