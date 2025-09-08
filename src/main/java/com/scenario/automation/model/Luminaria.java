package com.scenario.automation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "luminaires")
public class Luminaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Tipo é obrigatório")
    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Boolean status = false; // false = desligado, true = ligado

    @Min(value = 0, message = "Brilho deve ser entre 0 e 100")
    @Max(value = 100, message = "Brilho deve ser entre 0 e 100")
    @Column(nullable = false)
    private Integer brightness = 0;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Cor deve estar no formato hexadecimal (#FFFFFF)")
    @Column(length = 7)
    private String color = "#FFFFFF";

    @DecimalMin(value = "0.0", message = "Posição X deve ser maior ou igual a 0")
    @Column(name = "position_x")
    private Double positionX = 0.0;

    @DecimalMin(value = "0.0", message = "Posição Y deve ser maior ou igual a 0")
    @Column(name = "position_y")
    private Double positionY = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    @JsonBackReference
    private Ambiente ambiente;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtores
    public Luminaria() {}

    public Luminaria(String name, String type, Ambiente ambiente) {
        this.name = name;
        this.type = type;
        this.ambiente = ambiente;
    }

    public Luminaria(String name, String type, Boolean status, Integer brightness, String color, Double positionX, Double positionY, Ambiente ambiente) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.brightness = brightness;
        this.color = color;
        this.positionX = positionX;
        this.positionY = positionY;
        this.ambiente = ambiente;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
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

    // Métodos auxiliares
    public void turnOn() {
        this.status = true;
    }

    public void turnOff() {
        this.status = false;
    }

    public boolean isOn() {
        return status != null && status;
    }

    @Override
    public String toString() {
        return "Luminaria{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", brightness=" + brightness +
                ", color='" + color + '\'' +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
