package com.fish_diseases.biodata_service.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una especie de pez registrada en el sistema. Incluye
 * información taxonómica, nombres común y científico, distribución geográfica,
 * así como su relación con los parásitos que puede hospedar.
 */
@Entity
@Table(name = "fishes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fish {

	/**
	 * Identificador único del pez.
	 */
	@Id
	@Column(name = "fish_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Nombre científico del pez. Debe ser único y no nulo.
	 */
	@EqualsAndHashCode.Include
	@Column(name = "scientific_name", unique = true, nullable = false)
	private String scientificName;

	/**
	 * Nombre común del pez.
	 */
	@Column(name = "common_name")
	private String commonName;

	/**
	 * Información taxonómica embebida del pez.
	 */
	@Embedded
	private Taxonomy taxonomy;

	/**
	 * Distribución geográfica según la clasificación de la FAO.
	 */
	@Column(name = "fao_distribution", columnDefinition = "TEXT")
	private String faoDistribution;

	/**
	 * Conjunto de parásitos asociados a este pez como hospedador.
	 */
	@ManyToMany(mappedBy = "fishes")
	@JsonIgnoreProperties({ "fishes", "handler", "hibernateLazyInitializer" })
	@Builder.Default
	private Set<Parasite> parasites = new HashSet<>();

	/**
	 * Fecha y hora en que se creó el registro. Se asigna automáticamente antes de
	 * persistir.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * Fecha y hora de la última actualización del registro. Se actualiza
	 * automáticamente antes de cada modificación.
	 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/**
	 * Método de callback que se ejecuta antes de insertar un nuevo registro. Asigna
	 * la fecha y hora actual al campo {@code createdAt}.
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
