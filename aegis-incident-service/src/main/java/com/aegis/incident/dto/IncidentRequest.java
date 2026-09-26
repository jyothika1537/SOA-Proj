package com.aegis.incident.dto;

import com.aegis.incident.entity.IncidentStatus;
import com.aegis.incident.entity.IncidentType;
import com.aegis.incident.entity.ResourceType;
import com.aegis.incident.entity.Severity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncidentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Incident type is required")
    private IncidentType incidentType;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotBlank(message = "Location is required")
    private String location;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotBlank(message = "Reported by is required")
    private String reportedBy;

    @NotNull(message = "Required resource type is required")
    private ResourceType requiredResourceType;

    @NotNull(message = "Required resource quantity is required")
    @Min(value = 1, message = "Resource quantity must be at least 1")
    private Integer requiredResourceQuantity;

    private IncidentStatus incidentStatus = IncidentStatus.REPORTED;
}
