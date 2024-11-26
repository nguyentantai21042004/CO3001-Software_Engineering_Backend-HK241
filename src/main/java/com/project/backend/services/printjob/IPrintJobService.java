package com.project.backend.services.printjob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.models.PrintJob;

public interface IPrintJobService {
    PrintJob Create(PrintJobCreateDTO printJobCreateDTO, Integer studentId) throws Exception;

    PrintJob Detail(Integer printJobId, Integer studentId) throws Exception;

    Page<PrintJob> Get(PageRequest pageRequest, String keyword, Integer studentId) throws Exception;

    Page<PrintJob> Get(PageRequest pageRequest, String keyword) throws Exception;
}
