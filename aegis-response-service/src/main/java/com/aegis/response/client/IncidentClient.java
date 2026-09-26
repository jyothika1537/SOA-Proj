package com.aegis.response.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "INCIDENT-SERVICE")
public interface IncidentClient {

    @GetMapping("/api/incidents/{id}")
    IncidentResponse getIncident(@PathVariable("id") String id);

    record IncidentResponse(
            String incidentId,
            String title,
            String description,
            String incidentType,
            String severity,
            String location,
            String reportedBy,
            String incidentStatus,
            String requiredResourceType,
            Integer requiredResourceQuantity
    ) {}
}
