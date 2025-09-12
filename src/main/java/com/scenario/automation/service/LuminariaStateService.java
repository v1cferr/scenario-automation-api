package com.scenario.automation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LuminariaStateService {
    
    private static final Logger logger = LoggerFactory.getLogger(LuminariaStateService.class);
    
    // Armazena o estado das luminárias em memória (id -> isOn)
    private final Map<Long, Boolean> luminariaStates = new ConcurrentHashMap<>();
    
    // Lista de clientes SSE conectados
    private final CopyOnWriteArrayList<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();
    
    // Executor para heartbeat
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    
    public LuminariaStateService() {
        // Iniciar heartbeat a cada 30 segundos
        heartbeatExecutor.scheduleAtFixedRate(() -> sendHeartbeat(), 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * Liga uma luminária
     */
    public void turnOnLuminaria(Long luminariaId) {
        logger.info("Ligando luminária {}", luminariaId);
        luminariaStates.put(luminariaId, true);
        broadcastStateChange(luminariaId, true);
    }
    
    /**
     * Desliga uma luminária
     */
    public void turnOffLuminaria(Long luminariaId) {
        logger.info("Desligando luminária {}", luminariaId);
        luminariaStates.put(luminariaId, false);
        broadcastStateChange(luminariaId, false);
    }
    
    /**
     * Alterna o estado de uma luminária
     */
    public boolean toggleLuminaria(Long luminariaId) {
        boolean currentState = luminariaStates.getOrDefault(luminariaId, false);
        boolean newState = !currentState;
        logger.info("Alternando luminária {} de {} para {}", luminariaId, currentState, newState);
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
        SseEmitter emitter = new SseEmitter(0L); // Timeout infinito (0L significa sem timeout)
        sseEmitters.add(emitter);
        
        logger.info("Novo cliente SSE conectado. Total de conexões: {}", sseEmitters.size());
        
        // Remove o emitter quando a conexão for fechada
        emitter.onCompletion(() -> {
            sseEmitters.remove(emitter);
            logger.info("Cliente SSE desconectado (completion). Total de conexões: {}", sseEmitters.size());
        });
        emitter.onTimeout(() -> {
            sseEmitters.remove(emitter);
            logger.info("Cliente SSE desconectado (timeout). Total de conexões: {}", sseEmitters.size());
        });
        emitter.onError((ex) -> {
            sseEmitters.remove(emitter);
            logger.error("Erro na conexão SSE. Total de conexões: {}. Erro: {}", sseEmitters.size(), ex.getMessage());
        });
        
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
            
            logger.info("Estado inicial enviado para novo cliente SSE");
        } catch (IOException e) {
            sseEmitters.remove(emitter);
            logger.error("Erro ao enviar estado inicial para cliente SSE: {}", e.getMessage());
        }
        
        return emitter;
    }
    
    /**
     * Transmite mudança de estado para todos os clientes conectados
     */
    private void broadcastStateChange(Long luminariaId, boolean isOn) {
        logger.info("🔥 BROADCASTING STATE CHANGE: luminária {} está {}", luminariaId, isOn ? "ligada" : "desligada");
        logger.info("📊 Clientes SSE conectados: {}", sseEmitters.size());
        
        LuminariaStateEvent event = new LuminariaStateEvent(
            "state_change", 
            luminariaId, 
            isOn, 
            LocalDateTime.now()
        );
        
        logger.info("📤 Evento criado: {}", event);
        
        AtomicInteger clientsRemoved = new AtomicInteger(0);
        AtomicInteger clientsNotified = new AtomicInteger(0);
        
        // Remove emitters que falharam
        sseEmitters.removeIf(emitter -> {
            try {
                logger.info("📤 Enviando para cliente SSE...");
                emitter.send(SseEmitter.event()
                    .name("state_change")
                    .data(event)
                    .id(String.valueOf(System.currentTimeMillis())));
                clientsNotified.incrementAndGet();
                logger.info("✅ Evento enviado com sucesso para cliente");
                return false; // Mantém na lista
            } catch (IOException e) {
                logger.warn("❌ Removendo cliente SSE devido a erro de envio: {}", e.getMessage());
                clientsRemoved.incrementAndGet();
                return true; // Remove da lista
            }
        });
        
        logger.info("🎯 RESULTADO BROADCAST: {} clientes notificados, {} clientes removidos", 
                   clientsNotified.get(), clientsRemoved.get());
    }
    
    /**
     * Envia heartbeat para todos os clientes conectados
     */
    private void sendHeartbeat() {
        if (sseEmitters.isEmpty()) {
            return;
        }
        
        logger.debug("Enviando heartbeat para {} clientes SSE", sseEmitters.size());
        
        AtomicInteger clientsRemoved = new AtomicInteger(0);
        AtomicInteger clientsNotified = new AtomicInteger(0);
        
        sseEmitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data("{\"type\":\"heartbeat\",\"timestamp\":\"" + LocalDateTime.now() + "\"}")
                    .id(String.valueOf(System.currentTimeMillis())));
                clientsNotified.incrementAndGet();
                return false; // Mantém na lista
            } catch (IOException e) {
                logger.warn("Removendo cliente SSE durante heartbeat: {}", e.getMessage());
                clientsRemoved.incrementAndGet();
                return true; // Remove da lista
            }
        });
        
        if (clientsRemoved.get() > 0) {
            logger.info("Heartbeat: {} clientes ativos, {} clientes removidos", 
                       clientsNotified.get(), clientsRemoved.get());
        }
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