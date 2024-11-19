package com.project.backend.controllers;

import com.project.backend.models.PageAllocation;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.pageallocation.PageAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/page-allocations")
@RequiredArgsConstructor
public class PageAllocationController {

    private final PageAllocationService pageAllocationService;

    // Create a new page allocation
    @PostMapping
    public ResponseEntity<ResponseObject> createPageAllocation(@RequestBody PageAllocation pageAllocation) {
        ResponseObject response = pageAllocationService.createPageAllocation(pageAllocation);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Update an existing page allocation (only if status is 'pending')
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePageAllocation(@PathVariable Long id, @RequestBody PageAllocation updatedAllocation) {
        Optional<PageAllocation> result = pageAllocationService.updatePageAllocation(id, updatedAllocation);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(400).body("Cannot update a non-pending page allocation");
        }
    }

    // Delete a page allocation (only if status is 'pending')
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePageAllocation(@PathVariable Long id) {
        boolean deleted = pageAllocationService.deletePageAllocation(id);
        if (deleted) {
            return ResponseEntity.ok("Page allocation deleted successfully.");
        } else {
            return ResponseEntity.status(400).body("Cannot delete a non-pending page allocation");
        }
    }

    // Retrieve all page allocations
    @GetMapping
    public ResponseEntity<List<PageAllocation>> getAllPageAllocations() {
        List<PageAllocation> allocations = pageAllocationService.getAllPageAllocations();
        return ResponseEntity.ok(allocations);
    }

    // Retrieve page allocations by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<PageAllocation>> getPageAllocationsByYear(@PathVariable short year) {
        List<PageAllocation> allocations = pageAllocationService.getPageAllocationsByYear(year);
        return ResponseEntity.ok(allocations);
    }

    // Retrieve page allocation by id
    @GetMapping("/{id}")
    public ResponseEntity<Optional<PageAllocation>> getPageAllocationById(@PathVariable Long id) {
        Optional<PageAllocation> allocation = pageAllocationService.getPageAllocationById(id);
        return ResponseEntity.ok(allocation);
    }
}
