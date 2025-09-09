package com.scenario.automation.dto.images;

import java.time.LocalDateTime;

/**
 * DTO para representar uma imagem de ambiente retornada da API de imagens
 */
public class EnvironmentImageDto {

    private Long id;
    private Long environmentId;
    private String imageName;
    private String fileName;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtores
    public EnvironmentImageDto() {}

    public EnvironmentImageDto(Long environmentId, String imageName, String fileName, String filePath, String contentType, Long fileSize) {
        this.environmentId = environmentId;
        this.imageName = imageName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gera URL para acessar a imagem na API de imagens
     */
    public String getImageUrl(String imagesApiBaseUrl) {
        if (fileName != null && !fileName.isEmpty()) {
            return imagesApiBaseUrl + "/api/images/file/" + fileName;
        }
        return null;
    }

    @Override
    public String toString() {
        return "EnvironmentImageDto{" +
                "id=" + id +
                ", environmentId=" + environmentId +
                ", imageName='" + imageName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
