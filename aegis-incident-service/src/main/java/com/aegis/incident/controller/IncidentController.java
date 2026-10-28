package com.aegis.incident.controller;

import com.aegis.incident.dto.IncidentRequest;
import com.aegis.incident.dto.IncidentResponse;
import com.aegis.incident.dto.IncidentStatusUpdateRequest;
import com.aegis.incident.entity.IncidentStatus;
import com.aegis.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.createIncident(request));
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable("id") String id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IncidentResponse>> getIncidentsByStatus(@PathVariable("status") IncidentStatus status) {
        return ResponseEntity.ok(incidentService.getIncidentsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(@PathVariable("id") String id,
                                                           @Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.ok(incidentService.updateIncident(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateIncidentStatus(@PathVariable("id") String id,
                                                               @Valid @RequestBody IncidentStatusUpdateRequest request) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable("id") String id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
