package com.aegis.response.dto;

import com.aegis.response.entity.ResponsePriority;
import com.aegis.response.entity.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResponse {
    private String responseId;
    private String incidentId;
    private String resourceId;
    private String responderId;
    private ResponseStatus responseStatus;
    private ResponsePriority priority;
    private LocalDateTime dispatchedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime completedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
