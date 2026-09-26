package com.aegis.response.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RESOURCE-SERVICE")
public interface ResourceClient {

    @PostMapping("/api/resources/allocate")
    AllocationResponse allocate(@RequestBody AllocationRequest request);

    @PostMapping("/api/resources/{resourceId}/release")
    AllocationResponse release(@PathVariable("resourceId") String resourceId,
                              @RequestParam("quantity") Integer quantity,
                              @RequestParam("incidentId") String incidentId);

    record AllocationRequest(String resourceId, Integer quantity, String incidentId) {}

    record AllocationResponse(boolean allocated, String resourceId, String incidentId, Integer quantity, String message) {}
}
