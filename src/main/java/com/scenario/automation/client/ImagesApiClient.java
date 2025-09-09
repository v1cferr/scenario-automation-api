package com.scenario.automation.client;

import com.scenario.automation.dto.images.EnvironmentImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client para comunicação com a API de imagens
 */
@Component
public class ImagesApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${app.images-api.base-url:http://localhost:8081}")
    private String imagesApiBaseUrl;

    public ImagesApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Upload de imagem para um ambiente
     */
    public EnvironmentImageDto uploadImage(Long environmentId, String imageName, MultipartFile file) {
        try {
            String url = imagesApiBaseUrl + "/api/images/upload";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("environmentId", environmentId);
            body.add("imageName", imageName);
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<EnvironmentImageDto> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                requestEntity, 
                EnvironmentImageDto.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar imagens de um ambiente
     */
    public List<EnvironmentImageDto> getImagesByEnvironment(Long environmentId) {
        try {
            String url = imagesApiBaseUrl + "/api/images/environment/" + environmentId;

            ResponseEntity<List<EnvironmentImageDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<EnvironmentImageDto>>() {}
            );

            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            // Se a API de imagens não estiver disponível, retorna lista vazia
            return Collections.emptyList();
        }
    }

    /**
     * Buscar imagem por ID
     */
    public EnvironmentImageDto getImageById(Long imageId) {
        try {
            String url = imagesApiBaseUrl + "/api/images/" + imageId;

            ResponseEntity<EnvironmentImageDto> response = restTemplate.getForEntity(url, EnvironmentImageDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao buscar imagem: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar imagem por nome em um ambiente
     */
    public EnvironmentImageDto getImageByNameAndEnvironment(Long environmentId, String imageName) {
        try {
            String url = imagesApiBaseUrl + "/api/images/environment/" + environmentId + "/name/" + imageName;

            ResponseEntity<EnvironmentImageDto> response = restTemplate.getForEntity(url, EnvironmentImageDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            return null; // Retorna null se não encontrar
        }
    }

    /**
     * Deletar imagem
     */
    public boolean deleteImage(Long imageId) {
        try {
            String url = imagesApiBaseUrl + "/api/images/" + imageId;

            restTemplate.delete(url);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    /**
     * Atualizar nome da imagem
     */
    public EnvironmentImageDto updateImageName(Long imageId, String newImageName) {
        try {
            String url = imagesApiBaseUrl + "/api/images/" + imageId + "/name";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("imageName", newImageName);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<EnvironmentImageDto> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                requestEntity,
                EnvironmentImageDto.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao atualizar nome da imagem: " + e.getMessage(), e);
        }
    }

    /**
     * Contar imagens de um ambiente
     */
    public long countImagesByEnvironment(Long environmentId) {
        try {
            String url = imagesApiBaseUrl + "/api/images/environment/" + environmentId + "/count";

            ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Long>>() {}
            );

            Map<String, Long> body = response.getBody();
            return body != null ? body.getOrDefault("count", 0L) : 0L;
        } catch (RestClientException e) {
            return 0L;
        }
    }

    /**
     * Verificar se a API de imagens está disponível
     */
    public boolean isImagesApiAvailable() {
        try {
            String url = imagesApiBaseUrl + "/api/images/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gerar URL da imagem
     */
    public String getImageUrl(String fileName) {
        return imagesApiBaseUrl + "/api/images/file/" + fileName;
    }
}
