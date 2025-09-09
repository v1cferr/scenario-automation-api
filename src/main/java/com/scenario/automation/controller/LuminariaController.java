package com.scenario.automation.controller;

import com.scenario.automation.model.Luminaria;
import com.scenario.automation.service.LuminariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/luminaires")
@CrossOrigin(origins = "*")
public class LuminariaController {

    @Autowired
    private LuminariaService luminariaService;

    /**
     * Criar nova luminária
     */
    @PostMapping
    public ResponseEntity<?> createLuminaria(@Valid @RequestBody Luminaria luminaria) {
        try {
            Luminaria novaLuminaria = luminariaService.createLuminaria(luminaria);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaLuminaria);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao criar luminária");
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
     * Listar todas as luminárias
     */
    @GetMapping
    public ResponseEntity<List<Luminaria>> getAllLuminarias() {
        List<Luminaria> luminarias = luminariaService.getAllLuminarias();
        return ResponseEntity.ok(luminarias);
    }

    /**
     * Buscar luminárias por ambiente
     */
    @GetMapping("/environment/{environmentId}")
    public ResponseEntity<List<Luminaria>> getLuminariasByEnvironment(@PathVariable Long environmentId) {
        List<Luminaria> luminarias = luminariaService.getLuminariasByEnvironmentId(environmentId);
        return ResponseEntity.ok(luminarias);
    }

    /**
     * Buscar luminária por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLuminariaById(@PathVariable Long id) {
        Optional<Luminaria> luminaria = luminariaService.getLuminariaById(id);
        if (luminaria.isPresent()) {
            return ResponseEntity.ok(luminaria.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Luminária não encontrada");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualizar luminária
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLuminaria(@PathVariable Long id, @Valid @RequestBody Luminaria luminariaAtualizada) {
        try {
            Luminaria luminaria = luminariaService.updateLuminaria(id, luminariaAtualizada);
            return ResponseEntity.ok(luminaria);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao atualizar luminária");
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
     * Deletar luminária
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLuminaria(@PathVariable Long id) {
        try {
            luminariaService.deleteLuminaria(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Luminária deletada com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao deletar luminária");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}