package com.aegis.incident.dto;

import com.aegis.incident.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentStatusUpdateRequest {

    @NotNull(message = "Incident status is required")
    private IncidentStatus incidentStatus;
}
