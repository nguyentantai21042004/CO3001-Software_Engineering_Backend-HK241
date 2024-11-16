package com.project.backend.services.pageallocation;

import com.project.backend.models.PageAllocation;
import com.project.backend.repositories.PageAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import com.project.backend.responses.ResponseObject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PageAllocationService implements IPageAllocationService {

    private final PageAllocationRepository pageAllocationRepository;

    @Autowired
    public PageAllocationService(PageAllocationRepository pageAllocationRepository) {
        this.pageAllocationRepository = pageAllocationRepository;
    }

    // Create a new page allocation
    @Override
    public ResponseObject createPageAllocation(PageAllocation pageAllocation) {
        ResponseObject response = new ResponseObject();

        // Check if the date is in the past
        if (pageAllocation.getDate().isBefore(LocalDate.now())) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Cannot insert a page allocation with a date in the past.");
            return response;
        }

        try {
            PageAllocation savedAllocation = pageAllocationRepository.save(pageAllocation);
            response.setStatus(HttpStatus.CREATED);
            response.setData(savedAllocation);
            response.setMessage("Page allocation created successfully.");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage("An error occurred while saving the page allocation.");
        }

        return response;
    }

    // Update an existing page allocation (only if status is 'pending')
    @Override
    public Optional<PageAllocation> updatePageAllocation(Long id, PageAllocation updatedAllocation) {
        Optional<PageAllocation> existingAllocationOpt = pageAllocationRepository.findById(id);
        if (existingAllocationOpt.isPresent()) {
            PageAllocation existingAllocation = existingAllocationOpt.get();

            if (!"pending".equals(existingAllocation.getStatus())) {
                return Optional.empty();  // Cannot update if status is not 'pending'
            }

            // Update fields
            existingAllocation.setSemester(updatedAllocation.getSemester());
            existingAllocation.setYear(updatedAllocation.getYear());
            existingAllocation.setNumberOfPages(updatedAllocation.getNumberOfPages());
            existingAllocation.setDate(updatedAllocation.getDate());
            existingAllocation.setSpso(updatedAllocation.getSpso());

            pageAllocationRepository.save(existingAllocation);
            return Optional.of(existingAllocation);
        } else {
            return Optional.empty();
        }
    }

    // Delete a page allocation (only if status is 'pending')
    @Override
    public boolean deletePageAllocation(Long id) {
        Optional<PageAllocation> existingAllocationOpt = pageAllocationRepository.findById(id);
        if (existingAllocationOpt.isPresent()) {
            PageAllocation existingAllocation = existingAllocationOpt.get();

            if ("pending".equals(existingAllocation.getStatus())) {
                pageAllocationRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    // Find all page allocations
    @Override
    public List<PageAllocation> getAllPageAllocations() {
        return pageAllocationRepository.findAll();
    }

    // Find page allocations by year
    @Override
    public List<PageAllocation> getPageAllocationsByYear(short year) {
        return pageAllocationRepository.findByYear(year);
    }

    // Find page allocation by id
    @Override
    public Optional<PageAllocation> getPageAllocationById(Long id) {
        return pageAllocationRepository.findById(id);
    }
}
