package com.scenario.automation.service;

import com.scenario.automation.client.ImagesApiClient;
import com.scenario.automation.dto.images.EnvironmentImageDto;
import com.scenario.automation.model.Ambiente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service para gerenciar imagens de ambientes
 * Faz a ponte entre a API principal e a API de imagens
 */
@Service
public class AmbienteImageService {

    @Autowired
    private ImagesApiClient imagesApiClient;

    @Autowired
    private AmbienteService ambienteService;

    /**
     * Upload de imagem para um ambiente
     */
    public EnvironmentImageDto uploadImageForEnvironment(Long environmentId, String imageName, MultipartFile file) {
        // Verificar se o ambiente existe
        if (!ambienteService.existsById(environmentId)) {
            throw new RuntimeException("Ambiente não encontrado com ID: " + environmentId);
        }

        // Verificar se a API de imagens está disponível
        if (!imagesApiClient.isImagesApiAvailable()) {
            throw new RuntimeException("API de imagens não está disponível no momento");
        }

        // Fazer upload da imagem
        return imagesApiClient.uploadImage(environmentId, imageName, file);
    }

    /**
     * Buscar todas as imagens de um ambiente
     */
    public List<EnvironmentImageDto> getImagesByEnvironment(Long environmentId) {
        // Verificar se o ambiente existe
        if (!ambienteService.existsById(environmentId)) {
            throw new RuntimeException("Ambiente não encontrado com ID: " + environmentId);
        }

        return imagesApiClient.getImagesByEnvironment(environmentId);
    }

    /**
     * Buscar imagem principal do ambiente (primeira imagem)
     */
    public EnvironmentImageDto getPrimaryImageForEnvironment(Long environmentId) {
        List<EnvironmentImageDto> images = getImagesByEnvironment(environmentId);
        return images.isEmpty() ? null : images.get(0);
    }

    /**
     * Buscar imagem por nome em um ambiente
     */
    public EnvironmentImageDto getImageByNameAndEnvironment(Long environmentId, String imageName) {
        // Verificar se o ambiente existe
        if (!ambienteService.existsById(environmentId)) {
            throw new RuntimeException("Ambiente não encontrado com ID: " + environmentId);
        }

        return imagesApiClient.getImageByNameAndEnvironment(environmentId, imageName);
    }

    /**
     * Deletar imagem
     */
    public boolean deleteImage(Long imageId) {
        return imagesApiClient.deleteImage(imageId);
    }

    /**
     * Atualizar nome da imagem
     */
    public EnvironmentImageDto updateImageName(Long imageId, String newImageName) {
        return imagesApiClient.updateImageName(imageId, newImageName);
    }

    /**
     * Contar quantas imagens um ambiente possui
     */
    public long countImagesByEnvironment(Long environmentId) {
        if (!ambienteService.existsById(environmentId)) {
            return 0L;
        }

        return imagesApiClient.countImagesByEnvironment(environmentId);
    }

    /**
     * Verificar se um ambiente possui imagens
     */
    public boolean hasImages(Long environmentId) {
        return countImagesByEnvironment(environmentId) > 0;
    }

    /**
     * Obter URL da imagem principal do ambiente
     */
    public String getPrimaryImageUrl(Long environmentId) {
        EnvironmentImageDto primaryImage = getPrimaryImageForEnvironment(environmentId);
        if (primaryImage != null && primaryImage.getFileName() != null) {
            return imagesApiClient.getImageUrl(primaryImage.getFileName());
        }
        return null;
    }

    /**
     * Verificar se a API de imagens está disponível
     */
    public boolean isImagesApiAvailable() {
        return imagesApiClient.isImagesApiAvailable();
    }

    /**
     * Deletar todas as imagens de um ambiente
     * Útil quando um ambiente é excluído
     */
    public void deleteAllImagesFromEnvironment(Long environmentId) {
        List<EnvironmentImageDto> images = getImagesByEnvironment(environmentId);
        for (EnvironmentImageDto image : images) {
            deleteImage(image.getId());
        }
    }
}
