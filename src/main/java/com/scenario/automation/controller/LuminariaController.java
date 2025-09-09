package com.scenario.automation.controller;

import com.scenario.automation.model.Luminaria;
import com.scenario.automation.model.Ambiente;
import com.scenario.automation.service.LuminariaService;
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
@RequestMapping("/api/luminaires")
@CrossOrigin(origins = "*")
public class LuminariaController {

    @Autowired
    private LuminariaService luminariaService;

    @Autowired
    private AmbienteService ambienteService;

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
     * Buscar luminária por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLuminariaById(@PathVariable Long id) {
        try {
            Optional<Luminaria> luminaria = luminariaService.findById(id);
            if (luminaria.isPresent()) {
                return ResponseEntity.ok(luminaria.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Luminária não encontrada");
                error.put("message", "Nenhuma luminária encontrada com ID: " + id);
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
     * Listar todas as luminárias
     */
    @GetMapping
    public ResponseEntity<?> getAllLuminarias(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "environmentId", required = false) Long environmentId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) Boolean status) {
        try {
            List<Luminaria> luminarias;

            if (environmentId != null) {
                if (status != null && status) {
                    luminarias = luminariaService.findActiveLuminariasByAmbiente(environmentId);
                } else {
                    luminarias = luminariaService.findByAmbienteId(environmentId);
                }
            } else if (search != null && !search.trim().isEmpty()) {
                luminarias = luminariaService.searchLuminarias(search);
            } else if (type != null && !type.trim().isEmpty()) {
                luminarias = luminariaService.findByType(type);
            } else if (status != null && status) {
                luminarias = luminariaService.findActiveLuminarias();
            } else if (page >= 0 && size > 0 && size <= 100) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Luminaria> luminariaPage = luminariaService.findAll(pageable);
                return ResponseEntity.ok(luminariaPage);
            } else {
                luminarias = luminariaService.findAll();
            }

            return ResponseEntity.ok(luminarias);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar luminárias");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Atualizar luminária
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLuminaria(@PathVariable Long id, @Valid @RequestBody Luminaria luminaria) {
        try {
            Luminaria luminariaAtualizada = luminariaService.updateLuminaria(id, luminaria);
            return ResponseEntity.ok(luminariaAtualizada);
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

    // ---------------------------------- DEL -----------------------------

    /**
     * Ligar luminária
     */
    // Retirar o boolean
    // @PatchMapping("/{id}/turn-on")
    // public ResponseEntity<?> turnOnLuminaria(@PathVariable Long id) {
    //     try {
    //         Luminaria luminaria = luminariaService.turnOnLuminaria(id);
    //         return ResponseEntity.ok(luminaria);
    //     } catch (RuntimeException e) {
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", "Erro ao ligar luminária");
    //         error.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(error);
    //     } catch (Exception e) {
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", "Erro interno do servidor");
    //         error.put("message", e.getMessage());
    //         return ResponseEntity.internalServerError().body(error);
    //     }
    // }

    /**
     * Desligar luminária
     */
    // @PatchMapping("/{id}/turn-off")
    // public ResponseEntity<?> turnOffLuminaria(@PathVariable Long id) {
    //     try {
    //         Luminaria luminaria = luminariaService.turnOffLuminaria(id);
    //         return ResponseEntity.ok(luminaria);
    //     } catch (RuntimeException e) {
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", "Erro ao desligar luminária");
    //         error.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(error);
    //     } catch (Exception e) {
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", "Erro interno do servidor");
    //         error.put("message", e.getMessage());
    //         return ResponseEntity.internalServerError().body(error);
    //     }
    // }

    /**
     * Alterar brilho da luminária
     */
    @PatchMapping("/{id}/brightness")
    public ResponseEntity<?> changeBrightness(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer brightness = request.get("brightness");
            if (brightness == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Brilho é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }

            Luminaria luminaria = luminariaService.changeBrightness(id, brightness);
            return ResponseEntity.ok(luminaria);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao alterar brilho");
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
     * Alterar cor da luminária
     */
    @PatchMapping("/{id}/color")
    public ResponseEntity<?> changeColor(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String color = request.get("color");
            if (color == null || color.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Cor é obrigatória");
                return ResponseEntity.badRequest().body(error);
            }

            Luminaria luminaria = luminariaService.changeColor(id, color);
            return ResponseEntity.ok(luminaria);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao alterar cor");
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
     * Buscar luminárias por ambiente (endpoint específico)
     */
    @GetMapping("/environment/{environmentId}")
    public ResponseEntity<?> getLuminariasByEnvironment(@PathVariable Long environmentId) {
        try {
            List<Luminaria> luminarias = luminariaService.findByAmbienteId(environmentId);
            return ResponseEntity.ok(luminarias);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar luminárias do ambiente");
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
     * Estatísticas de luminárias por ambiente
     */
    @GetMapping("/environment/{environmentId}/stats")
    public ResponseEntity<?> getLuminariaStats(@PathVariable Long environmentId) {
        try {
            long totalLuminarias = luminariaService.countByAmbiente(environmentId);
            long luminariaLigadas = luminariaService.countActiveLuminariasByAmbiente(environmentId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("environmentId", environmentId);
            stats.put("total", totalLuminarias);
            stats.put("active", luminariaLigadas);
            stats.put("inactive", totalLuminarias - luminariaLigadas);
            
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar estatísticas");
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
