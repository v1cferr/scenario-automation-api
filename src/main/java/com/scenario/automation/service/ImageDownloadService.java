package com.scenario.automation.service;

import com.scenario.automation.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para gerar URLs temporárias de download de imagens
 */
@Service
public class ImageDownloadService {

    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Value("${app.images-api.base-url:http://localhost:8081}")
    private String imagesApiBaseUrl;
    
    // Tempo de expiração do token de download: 10 minutos
    private static final long DOWNLOAD_TOKEN_EXPIRATION = 600000; // 10 minutos em millisegundos

    /**
     * Gera uma URL temporária para download de imagem de um ambiente específico
     * @param environmentId ID do ambiente
     * @param imageName Nome da imagem
     * @return URL completa com token temporário
     */
    public String generateTemporaryDownloadUrl(Long environmentId, String imageName) {
        // Criar claims específicos para esta imagem
        Map<String, Object> claims = new HashMap<>();
        claims.put("environmentId", environmentId);
        claims.put("imageName", imageName);
        claims.put("type", "download");
        claims.put("exp", System.currentTimeMillis() + DOWNLOAD_TOKEN_EXPIRATION);
        
        // Gerar token específico para esta imagem
        String downloadToken = tokenProvider.generateTokenWithClaims("image-download", claims, DOWNLOAD_TOKEN_EXPIRATION);
        
        // Construir URL no formato: localhost:8080/api/environments/{environmentId}/{imageName}?token={JWT}
        return String.format("%s/api/environments/%d/image/%s?token=%s", 
                getCurrentApiBaseUrl(), environmentId, imageName, downloadToken);
    }

    /**
     * Gera uma URL temporária para download da imagem principal de um ambiente
     * @param environmentId ID do ambiente
     * @return URL completa com token temporário
     */
    public String generateTemporaryDownloadUrlForEnvironment(Long environmentId) {
        // Criar claims específicos para este ambiente (qualquer imagem)
        Map<String, Object> claims = new HashMap<>();
        claims.put("environmentId", environmentId);
        claims.put("type", "environment-download");
        claims.put("exp", System.currentTimeMillis() + DOWNLOAD_TOKEN_EXPIRATION);
        
        // Gerar token específico para este ambiente
        String downloadToken = tokenProvider.generateTokenWithClaims("environment-download", claims, DOWNLOAD_TOKEN_EXPIRATION);
        
        // Construir URL no formato: localhost:8080/api/environments/{environmentId}/serve-image?token={JWT}
        return String.format("%s/api/environments/%d/serve-image?token=%s", 
                getCurrentApiBaseUrl(), environmentId, downloadToken);
    }

    /**
     * Valida se um token de download é válido para um ambiente e imagem específicos
     * @param token Token JWT
     * @param environmentId ID do ambiente
     * @param imageName Nome da imagem (opcional)
     * @return true se o token é válido
     */
    public boolean validateDownloadToken(String token, Long environmentId, String imageName) {
        try {
            if (!tokenProvider.validateToken(token)) {
                return false;
            }
            
            Map<String, Object> claims = tokenProvider.getClaimsFromToken(token);
            
            // Verificar se o token não expirou
            Long expiration = (Long) claims.get("exp");
            if (expiration != null && System.currentTimeMillis() > expiration) {
                return false;
            }
            
            // Verificar se é um token de download
            String type = (String) claims.get("type");
            if (!"download".equals(type) && !"environment-download".equals(type)) {
                return false;
            }
            
            // Verificar se o environmentId corresponde
            Long tokenEnvironmentId = ((Number) claims.get("environmentId")).longValue();
            if (!environmentId.equals(tokenEnvironmentId)) {
                return false;
            }
            
            // Para tokens específicos de imagem, verificar o nome da imagem
            if ("download".equals(type) && imageName != null) {
                String tokenImageName = (String) claims.get("imageName");
                if (!imageName.equals(tokenImageName)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrai o environmentId de um token de download válido
     * @param token Token JWT
     * @return ID do ambiente ou null se inválido
     */
    public Long getEnvironmentIdFromToken(String token) {
        try {
            if (!tokenProvider.validateToken(token)) {
                return null;
            }
            
            Map<String, Object> claims = tokenProvider.getClaimsFromToken(token);
            return ((Number) claims.get("environmentId")).longValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtém a URL base da API atual
     * @return URL base da API de automação
     */
    private String getCurrentApiBaseUrl() {
        // Por padrão, assume localhost:8080, mas pode ser configurado
        return "http://localhost:8080";
    }
}
