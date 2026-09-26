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
        incidentClient.getIncident(request.getIncidentId());

        ResourceClient.AllocationRequest allocationRequest = new ResourceClient.AllocationRequest(
                request.getResourceId(), 1, request.getIncidentId());

        ResourceClient.AllocationResponse allocationResponse = resourceClient.allocate(allocationRequest);
        if (!allocationResponse.allocated()) {
            throw new IllegalStateException("Resource allocation failed: " + allocationResponse.message());
        }

        ResponseEntityModel model = ResponseEntityModel.builder()
                .incidentId(request.getIncidentId())
                .resourceId(request.getResourceId())
                .responderId(request.getResponderId())
                .responseStatus(ResponseStatus.RESOURCE_ALLOCATED)
                .priority(request.getPriority() != null ? request.getPriority() : ResponsePriority.MEDIUM)
                .notes(request.getNotes())
                .build();

        return mapToResponse(responseRepository.save(model));
    }

    public List<ResponseResponse> getAllResponses() {
        return responseRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ResponseResponse getResponseById(String id) {
        return mapToResponse(responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Response not found with id: " + id)));
    }

    public List<ResponseResponse> getResponsesByIncidentId(String incidentId) {
        return responseRepository.findByIncidentId(incidentId).stream().map(this::mapToResponse).toList();
    }

    public ResponseResponse updateStatus(String id, ResponseStatusUpdateRequest request) {
        ResponseEntityModel response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Response not found with id: " + id));
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

    private ResponseResponse mapToResponse(ResponseEntityModel response) {
        return ResponseResponse.builder()
                .responseId(response.getResponseId())
                .incidentId(response.getIncidentId())
                .resourceId(response.getResourceId())
                .responderId(response.getResponderId())
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
