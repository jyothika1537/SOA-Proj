package com.aegis.incident.service;

import com.aegis.incident.dto.IncidentRequest;
import com.aegis.incident.dto.IncidentResponse;
import com.aegis.incident.dto.IncidentStatusUpdateRequest;
import com.aegis.incident.entity.Incident;
import com.aegis.incident.entity.IncidentStatus;
import com.aegis.incident.repository.IncidentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentResponse createIncident(IncidentRequest request) {
        Incident incident = Incident.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .incidentType(request.getIncidentType())
                .severity(request.getSeverity())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .reportedBy(request.getReportedBy())
                .incidentStatus(request.getIncidentStatus() != null ? request.getIncidentStatus() : IncidentStatus.REPORTED)
                .requiredResourceType(request.getRequiredResourceType())
                .requiredResourceQuantity(request.getRequiredResourceQuantity())
                .build();

        return mapToResponse(incidentRepository.save(incident));
    }

    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public IncidentResponse getIncidentById(String id) {
        return mapToResponse(incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id)));
    }

    public List<IncidentResponse> getIncidentsByStatus(IncidentStatus status) {
        return incidentRepository.findByIncidentStatus(status).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public IncidentResponse updateIncident(String id, IncidentRequest request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setIncidentType(request.getIncidentType());
        incident.setSeverity(request.getSeverity());
        incident.setLocation(request.getLocation());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());
        incident.setReportedBy(request.getReportedBy());
        incident.setRequiredResourceType(request.getRequiredResourceType());
        incident.setRequiredResourceQuantity(request.getRequiredResourceQuantity());
        return mapToResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(String id, IncidentStatusUpdateRequest request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));
        incident.setIncidentStatus(request.getIncidentStatus());
        return mapToResponse(incidentRepository.save(incident));
    }

    @Transactional
    public void deleteIncident(String id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));
        if (incident.getIncidentStatus() == IncidentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an incident that has an active response");
        }
        incidentRepository.delete(incident);
    }

    private IncidentResponse mapToResponse(Incident incident) {
        return IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .incidentType(incident.getIncidentType())
                .severity(incident.getSeverity())
                .location(incident.getLocation())
                .latitude(incident.getLatitude())
                .longitude(incident.getLongitude())
                .reportedBy(incident.getReportedBy())
                .incidentStatus(incident.getIncidentStatus())
                .requiredResourceType(incident.getRequiredResourceType())
                .requiredResourceQuantity(incident.getRequiredResourceQuantity())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}
