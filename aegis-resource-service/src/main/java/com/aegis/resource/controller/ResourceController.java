package com.aegis.resource.controller;

import com.aegis.resource.dto.AllocationRequest;
import com.aegis.resource.dto.AllocationResponse;
import com.aegis.resource.dto.ResourceRequest;
import com.aegis.resource.dto.ResourceResponse;
import com.aegis.resource.entity.ResourceType;
import com.aegis.resource.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.createResource(request));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable("resourceId") String resourceId) {
        return ResponseEntity.ok(resourceService.getResourceById(resourceId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByType(@PathVariable("type") ResourceType type) {
        return ResponseEntity.ok(resourceService.getResourcesByType(type));
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(@PathVariable("resourceId") String resourceId,
                                                           @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateResource(resourceId, request));
    }

    @PostMapping("/allocate")
    public ResponseEntity<AllocationResponse> allocateResource(@Valid @RequestBody AllocationRequest request) {
        return ResponseEntity.ok(resourceService.allocateResource(request));
    }

    @PostMapping("/{resourceId}/release")
    public ResponseEntity<AllocationResponse> releaseResource(@PathVariable("resourceId") String resourceId,
                                                            @RequestParam Integer quantity,
                                                            @RequestParam String incidentId) {
        return ResponseEntity.ok(resourceService.releaseResource(resourceId, quantity, incidentId));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable("resourceId") String resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
}
