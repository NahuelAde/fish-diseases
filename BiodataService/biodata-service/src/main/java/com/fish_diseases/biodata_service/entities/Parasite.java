package com.fish_diseases.biodata_service.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un parásito documentado en el sistema.
 * Contiene información científica, taxonómica, y biológica, además de su relación con peces hospedadores.
 */
@Entity
@Table(name = "parasites")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Parasite {

    /**
     * Identificador único del parásito.
     */
	@Id
	@Column(name = "parasite_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    /**
     * Nombre científico del parásito. Debe ser único y no nulo.
     */
	@EqualsAndHashCode.Include
	@Column(name = "scientific_name", unique = true, nullable = false)
	private String scientificName;

    /**
     * Nombre común del parásito.
     */
	@Column(name = "common_name")
	private String commonName;

    /**
     * Información taxonómica embebida del parásito.
     */
	@Embedded
	private Taxonomy taxonomy;

    /**
     * Características diagnósticas del parásito, útiles para su identificación.
     */
    @Column(columnDefinition = "TEXT")
    private String diagnosticFeatures;

    /**
     * Tamaño típico del parásito.
     */
    private String size;

    /**
     * Distribución geográfica del parásito.
     */
    @Column(columnDefinition = "TEXT")
    private String geographicalDistribution;

    /**
     * Ciclo biológico del parásito.
     */
    @Column(columnDefinition = "TEXT")
    private String biologicalCycle;

    /**
     * Especificidad del hospedador.
     */
    @Column(name = "host_specificity", columnDefinition = "TEXT")
    private String hostSpecificity;

    /**
     * Conjunto de peces asociados como hospedadores de este parásito.
     */
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "parasites", "handler", "hibernateLazyInitializer" })
	@JoinTable(name = "parasite_fishes", joinColumns = @JoinColumn(name = "parasite_id"), inverseJoinColumns = @JoinColumn(name = "fish_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"parasite_id", "fish_id" }))
	@Builder.Default
	private Set<Fish> fishes = new HashSet<>();

    /**
     * Ubicación específica del parásito dentro del hospedador.
     */
    @Column(name = "location_on_host", columnDefinition = "TEXT")
    private String locationOnHost;

    /**
     * Daños que el parásito causa al hospedador.
     */
    @Column(name = "damages_on_host", columnDefinition = "TEXT")
    private String damagesOnHost;

    /**
     * Métodos de detección utilizados para identificar la presencia del parásito.
     */
    @Column(columnDefinition = "TEXT")
    private String detection;

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
