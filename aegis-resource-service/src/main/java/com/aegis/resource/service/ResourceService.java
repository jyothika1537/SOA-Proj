package com.aegis.resource.service;

import com.aegis.resource.dto.AllocationRequest;
import com.aegis.resource.dto.AllocationResponse;
import com.aegis.resource.dto.ResourceRequest;
import com.aegis.resource.dto.ResourceResponse;
import com.aegis.resource.entity.Resource;
import com.aegis.resource.entity.ResourceStatus;
import com.aegis.resource.entity.ResourceType;
import com.aegis.resource.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceResponse createResource(ResourceRequest request) {
        Resource resource = Resource.builder()
                .resourceName(request.getResourceName())
                .resourceType(request.getResourceType())
                .quantity(request.getQuantity())
                .availableQuantity(request.getQuantity())
                .deploymentZone(request.getDeploymentZone())
                .status(request.getStatus() != null ? request.getStatus() : ResourceStatus.AVAILABLE)
                .build();

        return mapToResponse(resourceRepository.save(resource));
    }

    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ResourceResponse getResourceById(String resourceId) {
        return mapToResponse(resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + resourceId)));
    }

    public List<ResourceResponse> getResourcesByType(ResourceType type) {
        return resourceRepository.findByResourceType(type).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public ResourceResponse updateResource(String resourceId, ResourceRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + resourceId));
        int allocatedQuantity = resource.getQuantity() - resource.getAvailableQuantity();
        if (request.getQuantity() < allocatedQuantity) {
            throw new IllegalArgumentException("Quantity cannot be lower than currently allocated quantity");
        }
        resource.setResourceName(request.getResourceName());
        resource.setResourceType(request.getResourceType());
        resource.setQuantity(request.getQuantity());
        resource.setAvailableQuantity(request.getQuantity() - allocatedQuantity);
        resource.setDeploymentZone(request.getDeploymentZone());
        resource.setStatus(request.getStatus());
        resource.setLastUpdated(LocalDateTime.now());
        return mapToResponse(resourceRepository.save(resource));
    }

    @Transactional
    public AllocationResponse allocateResource(AllocationRequest request) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + request.getResourceId()));

        if (resource.getStatus() != ResourceStatus.AVAILABLE
                || resource.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Resource is unavailable or has insufficient quantity");
        }

        int updated = resourceRepository.allocateQuantity(request.getResourceId(), request.getQuantity());
        if (updated == 0) {
            return AllocationResponse.builder()
                    .allocated(false)
                    .resourceId(resource.getResourceId())
                    .incidentId(request.getIncidentId())
                    .quantity(request.getQuantity())
                    .message("Resource allocation conflict")
                    .build();
        }

        Resource updatedResource = resourceRepository.findById(request.getResourceId()).orElse(resource);
        updatedResource.setAssignedIncidentId(request.getIncidentId());
        updatedResource.setStatus(updatedResource.getAvailableQuantity() > 0 ? ResourceStatus.AVAILABLE : ResourceStatus.IN_USE);
        updatedResource.setLastUpdated(LocalDateTime.now());
        resourceRepository.save(updatedResource);

        return AllocationResponse.builder()
                .allocated(true)
                .resourceId(resource.getResourceId())
                .incidentId(request.getIncidentId())
                .quantity(request.getQuantity())
                .message("Allocation successful")
                .build();
    }

    @Transactional
    public AllocationResponse releaseResource(String resourceId, Integer quantity, String incidentId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + resourceId));

        if (quantity == null || quantity < 1 || quantity > resource.getQuantity() - resource.getAvailableQuantity()) {
            throw new IllegalArgumentException("Invalid release quantity");
        }
        int updated = resourceRepository.releaseQuantity(resourceId, quantity);
        if (updated == 0) {
            return AllocationResponse.builder()
                    .allocated(false)
                    .resourceId(resourceId)
                    .incidentId(incidentId)
                    .quantity(quantity)
                    .message("Resource release failed")
                    .build();
        }

        Resource updatedResource = resourceRepository.findById(resourceId).orElse(resource);
        if (requestIncidentMismatch(updatedResource, incidentId)) {
            throw new IllegalArgumentException("Resource is not assigned to incident: " + incidentId);
        }
        updatedResource.setAssignedIncidentId(null);
        updatedResource.setStatus(ResourceStatus.AVAILABLE);
        updatedResource.setLastUpdated(LocalDateTime.now());
        resourceRepository.save(updatedResource);

        return AllocationResponse.builder()
                .allocated(true)
                .resourceId(resourceId)
                .incidentId(incidentId)
                .quantity(quantity)
                .message("Resource released successfully")
                .build();
    }

    @Transactional
    public void deleteResource(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + resourceId));
        if (resource.getAvailableQuantity() < resource.getQuantity()) {
            throw new IllegalStateException("Cannot delete an allocated resource");
        }
        resourceRepository.delete(resource);
    }

    private boolean requestIncidentMismatch(Resource resource, String incidentId) {
        return resource.getAssignedIncidentId() != null && !resource.getAssignedIncidentId().equals(incidentId);
    }

    private ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .resourceId(resource.getResourceId())
                .resourceName(resource.getResourceName())
                .resourceType(resource.getResourceType())
                .quantity(resource.getQuantity())
                .availableQuantity(resource.getAvailableQuantity())
                .deploymentZone(resource.getDeploymentZone())
                .status(resource.getStatus())
                .assignedIncidentId(resource.getAssignedIncidentId())
                .lastUpdated(resource.getLastUpdated())
                .build();
    }
}
