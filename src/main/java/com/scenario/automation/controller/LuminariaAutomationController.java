package com.scenario.automation.controller;

import com.scenario.automation.service.LuminariaStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/luminaires/automation")
@CrossOrigin(origins = "*")
public class LuminariaAutomationController {

    @Autowired
    private LuminariaStateService luminariaStateService;

    /**
     * Endpoint SSE para receber atualizações em tempo real do estado das luminárias
     */
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLuminariaEvents() {
        return luminariaStateService.addSseClient();
    }

    /**
     * Liga uma luminária
     */
    @PostMapping("/{id}/turn-on")
    public ResponseEntity<Map<String, Object>> turnOnLuminaria(@PathVariable Long id) {
        try {
            luminariaStateService.turnOnLuminaria(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Luminária ligada com sucesso");
            response.put("luminariaId", id);
            response.put("isOn", true);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao ligar luminária");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Desliga uma luminária
     */
    @PostMapping("/{id}/turn-off")
    public ResponseEntity<Map<String, Object>> turnOffLuminaria(@PathVariable Long id) {
        try {
            luminariaStateService.turnOffLuminaria(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Luminária desligada com sucesso");
            response.put("luminariaId", id);
            response.put("isOn", false);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao desligar luminária");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Alterna o estado de uma luminária (toggle)
     */
    @PostMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleLuminaria(@PathVariable Long id) {
        try {
            boolean newState = luminariaStateService.toggleLuminaria(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", newState ? "Luminária ligada" : "Luminária desligada");
            response.put("luminariaId", id);
            response.put("isOn", newState);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao alternar luminária");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtém o estado atual de uma luminária
     */
    @GetMapping("/{id}/state")
    public ResponseEntity<Map<String, Object>> getLuminariaState(@PathVariable Long id) {
        boolean isOn = luminariaStateService.getLuminariaState(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("luminariaId", id);
        response.put("isOn", isOn);
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém o estado de todas as luminárias
     */
    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> getAllLuminariaStates() {
        Map<Long, Boolean> states = luminariaStateService.getAllStates();
        
        Map<String, Object> response = new HashMap<>();
        response.put("states", states);
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}