package com.aegis.resource.dto;

import com.aegis.resource.entity.ResourceStatus;
import com.aegis.resource.entity.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private String resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private Integer quantity;
    private Integer availableQuantity;
    private String deploymentZone;
    private ResourceStatus status;
    private String assignedIncidentId;
    private LocalDateTime lastUpdated;
}
