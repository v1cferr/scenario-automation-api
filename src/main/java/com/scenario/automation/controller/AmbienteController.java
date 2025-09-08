package com.scenario.automation.controller;

import com.scenario.automation.model.Ambiente;
import com.scenario.automation.service.AmbienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/environments")
@CrossOrigin(origins = "*")
public class AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    /**
     * Criar novo ambiente
     */
    @PostMapping
    public ResponseEntity<?> createAmbiente(@Valid @RequestBody Ambiente ambiente) {
        try {
            Ambiente novoAmbiente = ambienteService.createAmbiente(ambiente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAmbiente);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao criar ambiente");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Buscar ambiente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAmbienteById(@PathVariable Long id) {
        try {
            Optional<Ambiente> ambiente = ambienteService.findById(id);
            if (ambiente.isPresent()) {
                return ResponseEntity.ok(ambiente.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ambiente não encontrado");
                error.put("message", "Nenhum ambiente encontrado com ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Listar todos os ambientes
     */
    @GetMapping
    public ResponseEntity<?> getAllAmbientes(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "search", required = false) String search) {
        try {
            if (search != null && !search.trim().isEmpty()) {
                List<Ambiente> ambientes = ambienteService.searchAmbientes(search);
                return ResponseEntity.ok(ambientes);
            } else if (page >= 0 && size > 0 && size <= 100) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Ambiente> ambientes = ambienteService.findAll(pageable);
                return ResponseEntity.ok(ambientes);
            } else {
                List<Ambiente> ambientes = ambienteService.findAll();
                return ResponseEntity.ok(ambientes);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar ambientes");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Buscar ambientes com suas luminárias
     */
    @GetMapping("/with-luminaires")
    public ResponseEntity<?> getAmbientesWithLuminarias() {
        try {
            List<Ambiente> ambientes = ambienteService.findAllWithLuminarias();
            return ResponseEntity.ok(ambientes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar ambientes com luminárias");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Atualizar ambiente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAmbiente(@PathVariable Long id, @Valid @RequestBody Ambiente ambiente) {
        try {
            Ambiente ambienteAtualizado = ambienteService.updateAmbiente(id, ambiente);
            return ResponseEntity.ok(ambienteAtualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao atualizar ambiente");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Deletar ambiente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAmbiente(@PathVariable Long id) {
        try {
            ambienteService.deleteAmbiente(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ambiente deletado com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao deletar ambiente");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Atualizar apenas a imagem do ambiente
     */
    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateAmbienteImage(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            if (imageUrl == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "URL da imagem é obrigatória");
                return ResponseEntity.badRequest().body(error);
            }

            Ambiente ambienteAtualizado = ambienteService.updateImageUrl(id, imageUrl);
            return ResponseEntity.ok(ambienteAtualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao atualizar imagem do ambiente");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Verificar se ambiente existe
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> checkAmbienteExists(@PathVariable Long id) {
        try {
            boolean exists = ambienteService.existsById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao verificar existência do ambiente");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
