package com.aegis.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationResponse {
    private boolean allocated;
    private String resourceId;
    private String incidentId;
    private Integer quantity;
    private String message;
}
