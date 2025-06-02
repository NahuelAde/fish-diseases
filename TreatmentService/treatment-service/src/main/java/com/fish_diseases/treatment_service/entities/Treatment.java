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
 * Entidad que representa un tratamiento utilizado para combatir enfermedades en
 * peces. Contiene información detallada sobre el nombre, descripción, espectro
 * de acción, dosis recomendada y las fechas de creación y actualización del
 * tratamiento.
 */
@Entity
@Table(name = "treatments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

	/**
	 * Identificador único del tratamiento.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * Nombre único del tratamiento.
	 */
	@Column(unique = true)
	private String name;

	/**
	 * Descripción general del tratamiento.
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Espectro de acción del tratamiento, es decir, el rango de organismos o
	 * enfermedades que puede tratar.
	 */
	@Column(name = "action_spectrum", columnDefinition = "TEXT")
	private String actionSpectrum;

	/**
	 * Dosis recomendada para aplicar el tratamiento.
	 */
	@Column(columnDefinition = "TEXT")
	private String dose;

	/**
	 * Fecha y hora en que se creó el registro del tratamiento. Se asigna
	 * automáticamente antes de persistir.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * Fecha y hora de la última actualización del registro del tratamiento. Se
	 * actualiza automáticamente antes de cada modificación.
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
