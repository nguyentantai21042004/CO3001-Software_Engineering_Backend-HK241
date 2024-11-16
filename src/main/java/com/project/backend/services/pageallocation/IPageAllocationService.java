package com.project.backend.services.pageallocation;

import com.project.backend.models.PageAllocation;
import com.project.backend.responses.ResponseObject;

import java.util.List;
import java.util.Optional;

public interface IPageAllocationService {

    ResponseObject createPageAllocation(PageAllocation pageAllocation);

    Optional<PageAllocation> updatePageAllocation(Long id, PageAllocation updatedAllocation);

    boolean deletePageAllocation(Long id);

    List<PageAllocation> getAllPageAllocations();

    List<PageAllocation> getPageAllocationsByYear(short year);

    Optional<PageAllocation> getPageAllocationById(Long id);
}
