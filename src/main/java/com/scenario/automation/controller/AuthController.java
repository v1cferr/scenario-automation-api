package com.scenario.automation.controller;

import com.scenario.automation.dto.JwtResponse;
import com.scenario.automation.dto.LoginRequest;
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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Credenciais hardcoded para demonstração
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "user123";

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Validar credenciais hardcoded
            if (!isValidCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciais inválidas");
                error.put("message", "Username ou password incorretos");
                return ResponseEntity.badRequest().body(error);
            }

            // Gerar token JWT
            String jwt = tokenProvider.generateToken(loginRequest.getUsername());
            long expirationTime = tokenProvider.getExpirationTime();

            return ResponseEntity.ok(new JwtResponse(jwt, loginRequest.getUsername(), expirationTime));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

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

    @GetMapping("/info")
    public ResponseEntity<?> getAuthInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("availableUsers", new String[]{ADMIN_USERNAME, USER_USERNAME});
        info.put("description", "Use 'admin/admin123' ou 'user/user123' para fazer login");
        info.put("tokenExpirationHours", tokenProvider.getExpirationTime() / (1000 * 60 * 60));
        return ResponseEntity.ok(info);
    }

    private boolean isValidCredentials(String username, String password) {
        return (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) ||
               (USER_USERNAME.equals(username) && USER_PASSWORD.equals(password));
    }
}
