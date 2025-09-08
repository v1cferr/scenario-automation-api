package com.scenario.automation.controller;

import com.scenario.automation.dto.JwtResponse;
import com.scenario.automation.dto.LoginRequest;
import com.scenario.automation.model.User;
import com.scenario.automation.repository.UserRepository;
import com.scenario.automation.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Login do usuário", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Buscar usuário no banco de dados
            Optional<User> userOpt = userRepository.findActiveUserByUsername(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciais inválidas");
                error.put("message", "Usuário não encontrado ou inativo");
                return ResponseEntity.badRequest().body(error);
            }

            User user = userOpt.get();
            
            // Verificar senha
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciais inválidas");
                error.put("message", "Senha incorreta");
                return ResponseEntity.badRequest().body(error);
            }

            // Gerar token JWT
            String jwt = tokenProvider.generateToken(user.getUsername());
            long expirationTime = tokenProvider.getExpirationTime();

            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), expirationTime));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Operation(summary = "Validar token", description = "Valida se o token JWT é válido")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                
                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("expiresAt", tokenProvider.getExpirationDateFromToken(token));
                    return ResponseEntity.ok(response);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token inválido ou expirado");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao validar token");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Operation(summary = "Informações de autenticação", description = "Retorna informações sobre usuários disponíveis")
    @GetMapping("/info")
    public ResponseEntity<?> getAuthInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("availableUsers", new String[]{"admin", "user", "demo"});
        info.put("passwords", new String[]{"admin123", "user123", "demo123"});
        info.put("description", "Usuários disponíveis para login");
        info.put("tokenExpirationHours", tokenProvider.getExpirationTime() / (1000 * 60 * 60));
        return ResponseEntity.ok(info);
    }
}
