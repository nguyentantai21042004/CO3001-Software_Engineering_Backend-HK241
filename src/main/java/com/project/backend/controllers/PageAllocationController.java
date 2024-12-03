package com.project.backend.controllers;

import com.project.backend.models.PageAllocation;
import com.project.backend.models.SPSO;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.pageallocation.PageAllocationService;
import com.project.backend.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/page-allocations")
@RequiredArgsConstructor
public class PageAllocationController {

        private final PageAllocationService pageAllocationService;
        private final AdminService adminService; // To fetch SPSO from the token

        // Create a new page allocation
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseObject> createPageAllocation(@RequestBody PageAllocation pageAllocation,
                                                                   @RequestHeader("Authorization") String authorizationHeader) throws Exception {
            // Extract token from the Authorization header
            String token = authorizationHeader.substring(7);

            // Fetch SPSO from token and set it in PageAllocation
            SPSO spso = adminService.getUserDetailsFromToken(token);
            pageAllocation.setSpso(spso);

            ResponseObject response = pageAllocationService.createPageAllocation(pageAllocation);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        // Update an existing page allocation (only if status is 'pending')
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> updatePageAllocation(@PathVariable Long id,
                                                      @RequestBody PageAllocation updatedAllocation,
                                                      @RequestHeader("Authorization") String authorizationHeader) throws Exception {
            // Extract token from the Authorization header
            String token = authorizationHeader.substring(7);

            // Fetch SPSO from token and set it in PageAllocation
            SPSO spso = adminService.getUserDetailsFromToken(token);
            updatedAllocation.setSpso(spso);

            Optional<PageAllocation> result = pageAllocationService.updatePageAllocation(id, updatedAllocation);
            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            } else {
                return ResponseEntity.status(400).body("Cannot update a non-pending page allocation");
            }
        }

        // Delete a page allocation (only if status is 'pending')
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
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
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PageAllocation>> getAllPageAllocations() {
            List<PageAllocation> allocations = pageAllocationService.getAllPageAllocations();
            return ResponseEntity.ok(allocations);
        }

        // Retrieve page allocations by year
        @GetMapping("/year/{year}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PageAllocation>> getPageAllocationsByYear(@PathVariable short year) {
            List<PageAllocation> allocations = pageAllocationService.getPageAllocationsByYear(year);
            return ResponseEntity.ok(allocations);
        }

        // Retrieve page allocation by id
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Optional<PageAllocation>> getPageAllocationById(@PathVariable Long id) {
            Optional<PageAllocation> allocation = pageAllocationService.getPageAllocationById(id);
            return ResponseEntity.ok(allocation);
        }
}
