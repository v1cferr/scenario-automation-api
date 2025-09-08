package com.scenario.automation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Scenario Automation API");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Scenario Automation API");
        info.put("description", "API para automação e gerenciamento de cenários de iluminação");
        info.put("version", "1.0.0");
        info.put("author", "v1cferr");
        info.put("documentation", "http://localhost:8080/api/docs");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("auth", "/api/auth");
        endpoints.put("environments", "/api/environments");
        endpoints.put("luminaires", "/api/luminaires");
        endpoints.put("health", "/api/health");
        
        info.put("endpoints", endpoints);
        return ResponseEntity.ok(info);
    }
}
