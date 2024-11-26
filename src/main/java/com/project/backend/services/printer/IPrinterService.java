package com.project.backend.services.printer;

import com.project.backend.models.Printer;
import com.project.backend.responses.ResponseObject;

public interface IPrinterService {

    ResponseObject findAllPrinters();

    ResponseObject findPrinterById(Integer id);

    Printer Detail(Integer id) throws Exception;

    ResponseObject addPrinter(Printer printer);

    ResponseObject updatePrinter(Integer id, Printer updatedPrinter);

    ResponseObject deletePrinter(Integer id);

    ResponseObject findByLocationId(Integer locationId);

    ResponseObject findBySpsoId(Integer spsoId);
}
