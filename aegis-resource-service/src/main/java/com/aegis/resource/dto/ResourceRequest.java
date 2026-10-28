package com.aegis.resource.dto;

import com.aegis.resource.entity.ResourceStatus;
import com.aegis.resource.entity.ResourceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Deployment zone is required")
    private String deploymentZone;

    private ResourceStatus status = ResourceStatus.AVAILABLE;
}
