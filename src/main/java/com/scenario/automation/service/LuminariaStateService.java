package com.scenario.automation.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LuminariaStateService {
    
    // Armazena o estado das luminárias em memória (id -> isOn)
    private final Map<Long, Boolean> luminariaStates = new ConcurrentHashMap<>();
    
    // Lista de clientes SSE conectados
    private final CopyOnWriteArrayList<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();
    
    /**
     * Liga uma luminária
     */
    public void turnOnLuminaria(Long luminariaId) {
        luminariaStates.put(luminariaId, true);
        broadcastStateChange(luminariaId, true);
    }
    
    /**
     * Desliga uma luminária
     */
    public void turnOffLuminaria(Long luminariaId) {
        luminariaStates.put(luminariaId, false);
        broadcastStateChange(luminariaId, false);
    }
    
    /**
     * Alterna o estado de uma luminária
     */
    public boolean toggleLuminaria(Long luminariaId) {
        boolean currentState = luminariaStates.getOrDefault(luminariaId, false);
        boolean newState = !currentState;
        luminariaStates.put(luminariaId, newState);
        broadcastStateChange(luminariaId, newState);
        return newState;
    }
    
    /**
     * Obtém o estado atual de uma luminária
     */
    public boolean getLuminariaState(Long luminariaId) {
        return luminariaStates.getOrDefault(luminariaId, false);
    }
    
    /**
     * Obtém todos os estados das luminárias
     */
    public Map<Long, Boolean> getAllStates() {
        return new ConcurrentHashMap<>(luminariaStates);
    }
    
    /**
     * Adiciona um novo cliente SSE
     */
    public SseEmitter addSseClient() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Timeout longo
        sseEmitters.add(emitter);
        
        // Remove o emitter quando a conexão for fechada
        emitter.onCompletion(() -> sseEmitters.remove(emitter));
        emitter.onTimeout(() -> sseEmitters.remove(emitter));
        emitter.onError((ex) -> sseEmitters.remove(emitter));
        
        // Envia o estado atual de todas as luminárias para o novo cliente
        try {
            LuminariaStateEvent initialEvent = new LuminariaStateEvent(
                "initial_state", 
                getAllStates(), 
                LocalDateTime.now()
            );
            emitter.send(SseEmitter.event()
                .name("initial_state")
                .data(initialEvent)
                .id(String.valueOf(System.currentTimeMillis())));
        } catch (IOException e) {
            sseEmitters.remove(emitter);
        }
        
        return emitter;
    }
    
    /**
     * Transmite mudança de estado para todos os clientes conectados
     */
    private void broadcastStateChange(Long luminariaId, boolean isOn) {
        LuminariaStateEvent event = new LuminariaStateEvent(
            "state_change", 
            luminariaId, 
            isOn, 
            LocalDateTime.now()
        );
        
        // Remove emitters que falharam
        sseEmitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("state_change")
                    .data(event)
                    .id(String.valueOf(System.currentTimeMillis())));
                return false; // Mantém na lista
            } catch (IOException e) {
                return true; // Remove da lista
            }
        });
    }
    
    /**
     * Classe para representar eventos de mudança de estado
     */
    public static class LuminariaStateEvent {
        private String eventType;
        private Long luminariaId;
        private Boolean isOn;
        private Map<Long, Boolean> allStates;
        private LocalDateTime timestamp;
        
        // Construtor para mudança de estado individual
        public LuminariaStateEvent(String eventType, Long luminariaId, Boolean isOn, LocalDateTime timestamp) {
            this.eventType = eventType;
            this.luminariaId = luminariaId;
            this.isOn = isOn;
            this.timestamp = timestamp;
        }
        
        // Construtor para estado inicial (todos os estados)
        public LuminariaStateEvent(String eventType, Map<Long, Boolean> allStates, LocalDateTime timestamp) {
            this.eventType = eventType;
            this.allStates = allStates;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getEventType() { return eventType; }
        public Long getLuminariaId() { return luminariaId; }
        public Boolean getIsOn() { return isOn; }
        public Map<Long, Boolean> getAllStates() { return allStates; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        // Setters
        public void setEventType(String eventType) { this.eventType = eventType; }
        public void setLuminariaId(Long luminariaId) { this.luminariaId = luminariaId; }
        public void setIsOn(Boolean isOn) { this.isOn = isOn; }
        public void setAllStates(Map<Long, Boolean> allStates) { this.allStates = allStates; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}