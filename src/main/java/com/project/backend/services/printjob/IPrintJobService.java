package com.project.backend.services.printjob;

import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.models.PrintJob;

public interface IPrintJobService {
    PrintJob createPrintJob(PrintJobCreateDTO printJobCreateDTO, Integer studentId) throws Exception;
}
