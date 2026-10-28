package com.aegis.response.service;

import com.aegis.response.client.IncidentClient;
import com.aegis.response.client.ResourceClient;
import com.aegis.response.dto.CreateResponseRequest;
import com.aegis.response.dto.ResponseResponse;
import com.aegis.response.dto.ResponseStatusUpdateRequest;
import com.aegis.response.entity.ResponseEntityModel;
import com.aegis.response.entity.ResponsePriority;
import com.aegis.response.entity.ResponseStatus;
import com.aegis.response.repository.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final IncidentClient incidentClient;
    private final ResourceClient resourceClient;

    @Transactional
    public ResponseResponse createResponse(CreateResponseRequest request) {
        IncidentClient.IncidentResponse incident;
        try {
            incident = incidentClient.getIncident(request.getIncidentId());
        } catch (FeignException.NotFound ex) {
            throw new jakarta.persistence.EntityNotFoundException("Incident not found with id: " + request.getIncidentId());
        }

        if (incident.requiredResourceQuantity() == null || incident.requiredResourceQuantity() < 1) {
            throw new IllegalArgumentException("Incident has no valid resource requirement");
        }
        ResourceClient.AllocationRequest allocationRequest = new ResourceClient.AllocationRequest(
                request.getResourceId(), incident.requiredResourceQuantity(), request.getIncidentId());

        ResourceClient.AllocationResponse allocationResponse = resourceClient.allocate(allocationRequest);
        if (!allocationResponse.allocated()) {
            throw new IllegalStateException("Resource allocation failed: " + allocationResponse.message());
        }

        ResponseEntityModel model = ResponseEntityModel.builder()
                .incidentId(request.getIncidentId())
                .resourceId(request.getResourceId())
                .responderId(request.getResponderId())
                .allocatedQuantity(allocationResponse.quantity())
                .responseStatus(ResponseStatus.RESOURCE_ALLOCATED)
                .priority(request.getPriority() != null ? request.getPriority() : ResponsePriority.MEDIUM)
                .notes(request.getNotes())
                .build();

        try {
            return mapToResponse(responseRepository.save(model));
        } catch (RuntimeException ex) {
            resourceClient.release(request.getResourceId(), allocationResponse.quantity(), request.getIncidentId());
            throw ex;
        }
    }

    public List<ResponseResponse> getAllResponses() {
        return responseRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ResponseResponse getResponseById(String id) {
        return mapToResponse(responseRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Response not found with id: " + id)));
    }

    public List<ResponseResponse> getResponsesByIncidentId(String incidentId) {
        return responseRepository.findByIncidentId(incidentId).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public ResponseResponse updateResponse(String id, CreateResponseRequest request) {
        ResponseEntityModel response = responseRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Response not found with id: " + id));
        response.setResponderId(request.getResponderId());
        response.setPriority(request.getPriority());
        response.setNotes(request.getNotes());
        return mapToResponse(responseRepository.save(response));
    }

    @Transactional
    public ResponseResponse updateStatus(String id, ResponseStatusUpdateRequest request) {
        ResponseEntityModel response = responseRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Response not found with id: " + id));
        validateTransition(response.getResponseStatus(), request.getResponseStatus());
        response.setResponseStatus(request.getResponseStatus());

        if (request.getResponseStatus() == ResponseStatus.DISPATCHED) {
            response.setDispatchedAt(LocalDateTime.now());
        } else if (request.getResponseStatus() == ResponseStatus.ON_SCENE) {
            response.setArrivedAt(LocalDateTime.now());
        } else if (request.getResponseStatus() == ResponseStatus.COMPLETED) {
            response.setCompletedAt(LocalDateTime.now());
        }

        return mapToResponse(responseRepository.save(response));
    }

    @Transactional
    public void deleteResponse(String id) {
        ResponseEntityModel response = responseRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Response not found with id: " + id));
        if (response.getResponseStatus() != ResponseStatus.CANCELLED
                && response.getResponseStatus() != ResponseStatus.COMPLETED) {
            throw new IllegalStateException("Only cancelled or completed responses can be deleted");
        }
        if (response.getResponseStatus() != ResponseStatus.COMPLETED) {
            resourceClient.release(response.getResourceId(), response.getAllocatedQuantity(), response.getIncidentId());
        }
        responseRepository.delete(response);
    }

    private void validateTransition(ResponseStatus current, ResponseStatus target) {
        if (target == ResponseStatus.CANCELLED) return;
        boolean valid = switch (current) {
            case RESOURCE_ALLOCATED -> target == ResponseStatus.DISPATCHED;
            case DISPATCHED -> target == ResponseStatus.ON_SCENE;
            case ON_SCENE -> target == ResponseStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException("Invalid response status transition: " + current + " -> " + target);
        }
    }

    private ResponseResponse mapToResponse(ResponseEntityModel response) {
        return ResponseResponse.builder()
                .responseId(response.getResponseId())
                .incidentId(response.getIncidentId())
                .resourceId(response.getResourceId())
                .responderId(response.getResponderId())
                .allocatedQuantity(response.getAllocatedQuantity())
                .responseStatus(response.getResponseStatus())
                .priority(response.getPriority())
                .dispatchedAt(response.getDispatchedAt())
                .arrivedAt(response.getArrivedAt())
                .completedAt(response.getCompletedAt())
                .notes(response.getNotes())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
