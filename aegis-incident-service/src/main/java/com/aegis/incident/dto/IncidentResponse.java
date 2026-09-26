package com.aegis.incident.dto;

import com.aegis.incident.entity.IncidentStatus;
import com.aegis.incident.entity.IncidentType;
import com.aegis.incident.entity.ResourceType;
import com.aegis.incident.entity.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {
    private String incidentId;
    private String title;
    private String description;
    private IncidentType incidentType;
    private Severity severity;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String reportedBy;
    private IncidentStatus incidentStatus;
    private ResourceType requiredResourceType;
    private Integer requiredResourceQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
