package com.scenario.automation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private Environment env;
    
    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Scenario Automation API");
        health.put("version", "1.0.0");
        
        // Informações do banco de dados
        Map<String, String> database = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            database.put("url", conn.getMetaData().getURL());
            database.put("driver", conn.getMetaData().getDriverName());
            database.put("product", conn.getMetaData().getDatabaseProductName());
            database.put("version", conn.getMetaData().getDatabaseProductVersion());
            database.put("status", "Connected");
        } catch (Exception e) {
            database.put("status", "Error: " + e.getMessage());
        }
        
        health.put("database", database);
        health.put("activeProfile", env.getProperty("spring.profiles.active", "default"));
        
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
