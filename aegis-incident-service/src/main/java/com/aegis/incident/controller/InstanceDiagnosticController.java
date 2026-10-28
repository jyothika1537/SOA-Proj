package com.aegis.incident.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InstanceDiagnosticController {
    @Value("${server.port}") private int port;
    @Value("${spring.application.name}") private String service;
    @Value("${eureka.instance.instance-id}") private String instanceId;

    @GetMapping("/api/incidents/_instance")
    public Map<String, Object> instance() {
        return Map.of("service", service, "instanceId", instanceId, "port", port);
    }
}
