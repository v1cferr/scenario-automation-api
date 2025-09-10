package com.scenario.automation.controller;

import com.scenario.automation.client.ImagesApiClient;
import com.scenario.automation.dto.images.EnvironmentImageDto;
import com.scenario.automation.model.Ambiente;
import com.scenario.automation.service.AmbienteImageService;
import com.scenario.automation.service.AmbienteService;
import com.scenario.automation.service.ImageDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.ArrayList;
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

    @Autowired
    private AmbienteImageService ambienteImageService;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Autowired
    private ImagesApiClient imagesApiClient;

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
     * Upload de imagem para um ambiente
     */
    @PostMapping("/{id}/images/upload")
    public ResponseEntity<?> uploadEnvironmentImage(
            @PathVariable Long id,
            @RequestParam("imageName") String imageName,
            @RequestParam("file") MultipartFile file) {
        try {
            EnvironmentImageDto savedImage = ambienteImageService.uploadImageForEnvironment(id, imageName, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao fazer upload da imagem");
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
     * Listar todas as imagens de um ambiente
     */
    @GetMapping("/{id}/images")
    public ResponseEntity<List<EnvironmentImageDto>> getEnvironmentImages(@PathVariable Long id) {
        try {
            List<EnvironmentImageDto> images = ambienteImageService.getImagesByEnvironment(id);
            return ResponseEntity.ok(images);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Buscar imagem principal do ambiente
     */
    @GetMapping("/{id}/images/primary")
    public ResponseEntity<?> getPrimaryEnvironmentImage(@PathVariable Long id) {
        try {
            EnvironmentImageDto primaryImage = ambienteImageService.getPrimaryImageForEnvironment(id);
            if (primaryImage != null) {
                return ResponseEntity.ok(primaryImage);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Nenhuma imagem encontrada para este ambiente");
                return ResponseEntity.ok(response);
            }
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar imagem principal");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Buscar imagem por nome
     */
    @GetMapping("/{id}/images/name/{imageName}")
    public ResponseEntity<?> getImageByName(@PathVariable Long id, @PathVariable String imageName) {
        try {
            EnvironmentImageDto image = ambienteImageService.getImageByNameAndEnvironment(id, imageName);
            if (image != null) {
                return ResponseEntity.ok(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar imagem");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Contar imagens de um ambiente
     */
    @GetMapping("/{id}/images/count")
    public ResponseEntity<Map<String, Long>> countEnvironmentImages(@PathVariable Long id) {
        try {
            long count = ambienteImageService.countImagesByEnvironment(id);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Long> response = new HashMap<>();
            response.put("count", 0L);
            return ResponseEntity.ok(response);
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

    /**
     * Gerar URL temporária para download da imagem principal do ambiente
     */
    @GetMapping("/{id}/image-url")
    public ResponseEntity<?> getEnvironmentImageUrl(@PathVariable Long id) {
        try {
            System.out.println("🔍 Gerando URL temporária para ambiente ID: " + id);
            
            // Verificar se o ambiente existe
            if (!ambienteService.existsById(id)) {
                System.out.println("❌ Ambiente " + id + " não encontrado");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ambiente não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Buscar imagens do ambiente
            List<EnvironmentImageDto> images = ambienteImageService.getImagesByEnvironment(id);
            System.out.println("📸 Imagens encontradas para ambiente " + id + ": " + images.size());
            
            if (images.isEmpty()) {
                System.out.println("⚠️ Nenhuma imagem encontrada para ambiente " + id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Nenhuma imagem encontrada para este ambiente");
                error.put("environmentId", id.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Usar a primeira imagem
            EnvironmentImageDto firstImage = images.get(0);
            System.out.println("🖼️ Gerando URL para imagem: " + firstImage.getFileName());
            
            // Solicitar URL temporária da API de imagens
            ResponseEntity<Map<String, Object>> tempUrlResponse = imagesApiClient.generateTemporaryImageUrl(id, firstImage.getFileName());
            
            if (tempUrlResponse.getStatusCode().is2xxSuccessful() && tempUrlResponse.getBody() != null) {
                Map<String, Object> tempUrlData = tempUrlResponse.getBody();
                System.out.println("✅ URL temporária gerada com sucesso");
                
                Map<String, Object> response = new HashMap<>();
                response.put("url", tempUrlData.get("url"));
                response.put("environmentId", id);
                response.put("fileName", firstImage.getFileName());
                response.put("imageName", firstImage.getImageName());
                response.put("expiresIn", "10 minutes");
                
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Falha ao gerar URL temporária na API de imagens");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Erro ao gerar URL temporária");
                error.put("environmentId", id.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao gerar URL: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao gerar URL da imagem");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Gerar URLs temporárias para todas as imagens de um ambiente
     */
    @GetMapping("/{id}/images-urls")
    public ResponseEntity<?> getAllEnvironmentImageUrls(@PathVariable Long id) {
        try {
            System.out.println("🔍 Gerando URLs temporárias para todas as imagens do ambiente ID: " + id);
            
            // Verificar se o ambiente existe
            if (!ambienteService.existsById(id)) {
                System.out.println("❌ Ambiente " + id + " não encontrado");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ambiente não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Buscar todas as imagens do ambiente
            List<EnvironmentImageDto> images = ambienteImageService.getImagesByEnvironment(id);
            System.out.println("📸 Imagens encontradas para ambiente " + id + ": " + images.size());
            
            if (images.isEmpty()) {
                System.out.println("⚠️ Nenhuma imagem encontrada para ambiente " + id);
                Map<String, Object> response = new HashMap<>();
                response.put("environmentId", id);
                response.put("images", new ArrayList<>());
                response.put("message", "Nenhuma imagem encontrada para este ambiente");
                return ResponseEntity.ok(response);
            }

            // Gerar URLs temporárias para todas as imagens
            List<Map<String, Object>> imageUrls = new ArrayList<>();
            
            for (EnvironmentImageDto image : images) {
                try {
                    System.out.println("🖼️ Gerando URL para imagem: " + image.getFileName());
                    
                    ResponseEntity<Map<String, Object>> tempUrlResponse = 
                        imagesApiClient.generateTemporaryImageUrl(id, image.getFileName());
                    
                    if (tempUrlResponse.getStatusCode().is2xxSuccessful() && tempUrlResponse.getBody() != null) {
                        Map<String, Object> tempUrlData = tempUrlResponse.getBody();
                        
                        Map<String, Object> imageData = new HashMap<>();
                        imageData.put("id", image.getId());
                        imageData.put("imageName", image.getImageName());
                        imageData.put("fileName", image.getFileName());
                        imageData.put("url", tempUrlData.get("url"));
                        imageData.put("description", image.getDescription());
                        imageData.put("fileSize", image.getFileSize());
                        imageData.put("contentType", image.getContentType());
                        imageData.put("createdAt", image.getCreatedAt());
                        
                        imageUrls.add(imageData);
                        System.out.println("✅ URL gerada para " + image.getFileName());
                    } else {
                        System.out.println("⚠️ Falha ao gerar URL para " + image.getFileName());
                    }
                } catch (Exception e) {
                    System.out.println("❌ Erro ao gerar URL para " + image.getFileName() + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("environmentId", id);
            response.put("images", imageUrls);
            response.put("totalImages", imageUrls.size());
            response.put("expiresIn", "10 minutes");
            
            System.out.println("✅ URLs geradas com sucesso: " + imageUrls.size() + " de " + images.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao gerar URLs: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao gerar URLs das imagens");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Servir imagem do ambiente com autenticação por token
     * Formato: /api/environments/{id}/serve-image?token={JWT}
     */
    @GetMapping("/{id}/serve-image")
    public ResponseEntity<?> serveEnvironmentImage(
            @PathVariable Long id,
            @RequestParam("token") String token) {
        try {
            System.out.println("🖼️ Tentativa de acesso à imagem do ambiente " + id);
            System.out.println("🔑 Token recebido: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            // Validar token
            boolean tokenValid = imageDownloadService.validateDownloadToken(token, id, null);
            System.out.println("🔍 Resultado da validação do token: " + tokenValid);
            
            if (!tokenValid) {
                System.out.println("❌ Token inválido para ambiente " + id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token inválido");
                error.put("environmentId", id.toString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            System.out.println("✅ Token válido para ambiente " + id);

            // Verificar se o ambiente existe
            boolean environmentExists = ambienteService.existsById(id);
            System.out.println("🏠 Ambiente " + id + " existe: " + environmentExists);
            
            if (!environmentExists) {
                System.out.println("❌ Ambiente " + id + " não encontrado");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ambiente não encontrado");
                error.put("environmentId", id.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Buscar primeira imagem do ambiente
            List<EnvironmentImageDto> images = ambienteImageService.getImagesByEnvironment(id);
            System.out.println("📸 Imagens encontradas para ambiente " + id + ": " + images.size());
            
            if (images.isEmpty()) {
                System.out.println("⚠️ Nenhuma imagem encontrada para ambiente " + id);
                
                // Para fins de teste, vamos tentar servir uma imagem padrão que existe
                System.out.println("🔄 Tentando servir imagem padrão do disco...");
                try {
                    ResponseEntity<Resource> response = proxyImageRequest("20250910_151045_4334a1f6.jpg");
                    System.out.println("📁 Resposta da imagem padrão: " + response.getStatusCode());
                    return response;
                } catch (Exception e) {
                    System.out.println("❌ Falha ao servir imagem padrão: " + e.getMessage());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Nenhuma imagem encontrada");
                    error.put("environmentId", id.toString());
                    error.put("details", "Tentativa de servir imagem padrão também falhou: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                }
            }

            System.out.println("📸 Servindo imagem: " + images.get(0).getFileName());
            // Fazer proxy para a Images API
            ResponseEntity<Resource> response = proxyImageRequest(images.get(0).getFileName());
            System.out.println("🔄 Resposta do proxy: " + response.getStatusCode());
            return response;

        } catch (Exception e) {
            System.out.println("❌ Erro ao servir imagem: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            error.put("environmentId", id.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Gerar URL temporária para download de uma imagem específica
     */
    @GetMapping("/{id}/image/{imageName}/url")
    public ResponseEntity<?> getSpecificImageUrl(
            @PathVariable Long id, 
            @PathVariable String imageName) {
        try {
            // Verificar se o ambiente existe
            if (!ambienteService.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ambiente não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Verificar se a imagem existe
            EnvironmentImageDto image = ambienteImageService.getImageByNameAndEnvironment(id, imageName);
            if (image == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Imagem não encontrada");
                return ResponseEntity.notFound().build();
            }

            // Gerar URL temporária específica
            String temporaryUrl = imageDownloadService.generateTemporaryDownloadUrl(id, imageName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", temporaryUrl);
            response.put("environmentId", id);
            response.put("imageName", imageName);
            response.put("expiresIn", "10 minutes");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao gerar URL da imagem");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Servir imagem específica do ambiente com autenticação por token
     * Formato: /api/environments/{id}/image/{imageName}?token={JWT}
     */
    @GetMapping("/{id}/image/{imageName}")
    public ResponseEntity<Resource> serveSpecificEnvironmentImage(
            @PathVariable Long id,
            @PathVariable String imageName,
            @RequestParam("token") String token) {
        try {
            // Validar token para esta imagem específica
            if (!imageDownloadService.validateDownloadToken(token, id, imageName)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Verificar se o ambiente existe
            if (!ambienteService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Verificar se a imagem existe
            EnvironmentImageDto image = ambienteImageService.getImageByNameAndEnvironment(id, imageName);
            if (image == null) {
                return ResponseEntity.notFound().build();
            }

            // Fazer proxy para a Images API
            return proxyImageRequest(image.getFileName());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Método auxiliar para fazer proxy da requisição para a Images API
     */
    private ResponseEntity<Resource> proxyImageRequest(String fileName) {
        try {
            System.out.println("🔄 Fazendo proxy para imagem: " + fileName);
            
            // Fazer chamada para a Images API usando o client
            ResponseEntity<Resource> response = imagesApiClient.getImageAsResource(fileName);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("✅ Imagem encontrada na Images API");
                
                // Determinar Content-Type baseado na extensão do arquivo
                String contentType = "application/octet-stream"; // default
                String lowerFileName = fileName.toLowerCase();
                if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (lowerFileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (lowerFileName.endsWith(".webp")) {
                    contentType = "image/webp";
                }
                
                System.out.println("📋 Content-Type definido: " + contentType);
                
                // Criar headers com Content-Type correto e CORS
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.set("Access-Control-Allow-Origin", "*");
                headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.set("Access-Control-Allow-Headers", "*");
                headers.setCacheControl("max-age=3600"); // Cache por 1 hora
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getBody());
            } else {
                System.out.println("❌ Imagem não encontrada na Images API: " + response.getStatusCode());
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro no proxy da imagem: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ENDPOINT DE TESTE - Validar segurança de tokens
     */
    @GetMapping("/test/token-security/{env1}/{env2}")
    public ResponseEntity<?> testTokenSecurity(@PathVariable Long env1, @PathVariable Long env2) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Gerar token para ambiente 1
            String url1 = imageDownloadService.generateTemporaryDownloadUrlForEnvironment(env1);
            String token1 = url1.substring(url1.indexOf("token=") + 6);
            
            // Testar token do ambiente 1 no ambiente 1 (deve funcionar)
            boolean valid1on1 = imageDownloadService.validateDownloadToken(token1, env1, null);
            
            // Testar token do ambiente 1 no ambiente 2 (deve falhar)
            boolean valid1on2 = imageDownloadService.validateDownloadToken(token1, env2, null);
            
            result.put("token_env" + env1 + "_on_env" + env1, valid1on1 ? "✅ VÁLIDO" : "❌ INVÁLIDO");
            result.put("token_env" + env1 + "_on_env" + env2, valid1on2 ? "❌ FALHA DE SEGURANÇA!" : "✅ BLOQUEADO (correto)");
            result.put("security_status", !valid1on2 ? "🔒 SEGURANÇA OK" : "⚠️ VULNERABILIDADE!");
            result.put("test_summary", "Token do ambiente " + env1 + " " + (valid1on2 ? "FUNCIONOU" : "foi BLOQUEADO") + " no ambiente " + env2);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro no teste de segurança: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * ENDPOINT DE TESTE SIMPLES - Testar roteamento
     */
    @GetMapping("/test/simple-image/{id}")
    public ResponseEntity<?> testSimpleImageEndpoint(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint funcionando");
        response.put("environmentId", id);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
