package com.aegis.response.controller;

import com.aegis.response.dto.CreateResponseRequest;
import com.aegis.response.dto.ResponseResponse;
import com.aegis.response.dto.ResponseStatusUpdateRequest;
import com.aegis.response.service.ResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @PostMapping
    public ResponseEntity<ResponseResponse> createResponse(@Valid @RequestBody CreateResponseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.createResponse(request));
    }

    @GetMapping
    public ResponseEntity<List<ResponseResponse>> getAllResponses() {
        return ResponseEntity.ok(responseService.getAllResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResponse> getResponseById(@PathVariable String id) {
        return ResponseEntity.ok(responseService.getResponseById(id));
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<ResponseResponse>> getResponsesByIncidentId(@PathVariable String incidentId) {
        return ResponseEntity.ok(responseService.getResponsesByIncidentId(incidentId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseResponse> updateStatus(@PathVariable String id,
                                                       @Valid @RequestBody ResponseStatusUpdateRequest request) {
        return ResponseEntity.ok(responseService.updateStatus(id, request));
    }
}
