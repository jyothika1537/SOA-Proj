package com.aegis.response.dto;

import com.aegis.response.entity.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResponseStatusUpdateRequest {

    @NotNull(message = "Response status is required")
    private ResponseStatus responseStatus;
}
