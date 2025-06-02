package com.fish_diseases.treatment_service.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un método de laboratorio utilizado para aplicar tratamientos o realizar análisis.
 * Incluye información como el nombre del método, la técnica utilizada y las fechas de creación y actualización.
 */
@Entity
@Table(name = "lab_methods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryMethod {

    /**
     * Identificador único del método de laboratorio.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

    /**
     * Nombre del método de laboratorio. Debe ser único.
     */
	@Column(unique = true)
	private String name;

    /**
     * Descripción detallada de la técnica empleada en el método de laboratorio.
     */
	@Column(columnDefinition = "TEXT")
	private String technique;

    /**
     * Fecha y hora en que se creó el registro. Se asigna automáticamente antes de persistir.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del registro. Se actualiza automáticamente antes de cada modificación.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método de callback que se ejecuta antes de insertar un nuevo registro.
     * Asigna la fecha y hora actual al campo {@code createdAt}.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Método de callback que se ejecuta antes de actualizar un registro existente.
     * Asigna la fecha y hora actual al campo {@code updatedAt}.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
