package com.aegis.response.dto;

import com.aegis.response.entity.ResponsePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateResponseRequest {

    @NotBlank(message = "Incident ID is required")
    private String incidentId;

    @NotBlank(message = "Resource ID is required")
    private String resourceId;

    @NotBlank(message = "Responder ID is required")
    private String responderId;

    private ResponsePriority priority = ResponsePriority.MEDIUM;

    private String notes;
}
