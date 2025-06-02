package com.fish_diseases.biodata_service.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fish_diseases.biodata_service.client.BioDataClient;
import com.fish_diseases.biodata_service.dtos.ApiFishDTO;
import com.fish_diseases.biodata_service.dtos.ApiTaxonomyDTO;
import com.fish_diseases.biodata_service.dtos.CreateParasiteDTO;
import com.fish_diseases.biodata_service.dtos.UpdateParasiteDTO;
import com.fish_diseases.biodata_service.entities.Fish;
import com.fish_diseases.biodata_service.entities.Parasite;
import com.fish_diseases.biodata_service.entities.Taxonomy;
import com.fish_diseases.biodata_service.exceptions.ParasiteNotFoundException;
import com.fish_diseases.biodata_service.mapper.FishMapper;
import com.fish_diseases.biodata_service.mapper.ParasiteMapper;
import com.fish_diseases.biodata_service.mapper.TaxonomyMapper;
import com.fish_diseases.biodata_service.repositories.FishRepository;
import com.fish_diseases.biodata_service.repositories.ParasiteRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servicio para la gestión de parásitos en el sistema.
 * Incluye operaciones de consulta, creación, actualización y eliminación de parásitos,
 * así como métodos auxiliares para integración con fuentes externas como WoRMS.
 */
@Service
public class ParasiteService {

	private static final Logger log = LoggerFactory.getLogger(ParasiteService.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private final ParasiteRepository parasiteRepository;
	private final FishRepository fishRepository;
	private final BioDataClient bioDataClient;
	private final FishMapper fishMapper;
	private final ParasiteMapper parasiteMapper;
	private final TaxonomyMapper taxonomyMapper;
	private final Validator validator;

	/**
	 * Constructor de {@code ParasiteService} que inicializa todas las dependencias requeridas.
	 *
	 * @param parasiteRepository repositorio para acceder a datos de parásitos
	 * @param fishRepository repositorio para acceder a datos de peces
	 * @param bioDataClient cliente para comunicarse con el servicio de datos biológicos
	 * @param fishMapper mapper para transformar entidades de peces y sus DTOs
	 * @param parasiteMapper mapper para transformar entidades de parásitos y sus DTOs
	 * @param taxonomyMapper mapper para convertir información taxonómica
	 * @param validator validador para aplicar reglas de validación a los datos
	 */
	public ParasiteService(ParasiteRepository parasiteRepository, FishRepository fishRepository,
			BioDataClient bioDataClient, FishMapper fishMapper, ParasiteMapper parasiteMapper,
			TaxonomyMapper taxonomyMapper, Validator validator) {
		this.parasiteRepository = parasiteRepository;
		this.fishRepository = fishRepository;
		this.bioDataClient = bioDataClient;
		this.fishMapper = fishMapper;
		this.parasiteMapper = parasiteMapper;
		this.taxonomyMapper = taxonomyMapper;
		this.validator = validator;
	}

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Recupera todos los parásitos registrados en el sistema.
	 *
	 * @return Lista de entidades Parasite.
	 */
	@Transactional(readOnly = true)
	public List<Parasite> findAll() {

		if (isTraceEnabled) {
			log.trace("######## Start findAll Parasite ########");
		}

		List<Parasite> allParasites = parasiteRepository.findAll();

		if (isTraceEnabled) {
			log.trace("######## End findAll Parasite ########");
		}

		return allParasites;
	}

	/**
	 * Recupera un parásito por su identificador único.
	 *
	 * @param id ID del parásito.
	 * @return Entidad Parasite correspondiente.
	 * @throws ParasiteNotFoundException si el parásito no existe.
	 */
	@Transactional(readOnly = true)
	public Parasite findById(Long id) {

		if (isTraceEnabled) {
			log.trace("######## Start findById Parasite ########");
		}

		Parasite parasite = parasiteRepository
				.findById(id)
				.orElseThrow(ParasiteNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findById Parasite ########");
		}

		return parasite;
	}

	/**
	 * Recupera un parásito por su nombre científico.
	 *
	 * @param scientificName Nombre científico del parásito.
	 * @return Entidad Parasite correspondiente.
	 * @throws ParasiteNotFoundException si no se encuentra el parásito.
	 */
	@Transactional(readOnly = true)
	public Parasite findByScientificName(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start findByScientificName Parasite ########");
		}

		Parasite parasite = parasiteRepository
				.findByScientificName(scientificName)
				.orElseThrow(ParasiteNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findByScientificName Parasite ########");
		}

		return parasite;
	}

	/**
	 * Obtiene una lista de peces hospedadores de un parásito, a partir del nombre científico del mismo.
	 *
	 * @param parasiteScientificName Nombre científico del parásito.
	 * @return Lista de entidades Fish asociadas al parásito.
	 */
	@Transactional(readOnly = true)
	public List<Fish> getFishesByParasiteScientificName(String parasiteScientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start getFishesByParasiteScientificName ########");
		}

		Parasite parasite = findByScientificName(parasiteScientificName);

		List<String> fishScientificNames = parasite
				.getFishes()
				.stream()
				.map(Fish::getScientificName)
				.collect(Collectors.toList());

		List<Fish> listFishByParasite = fishRepository.findByScientificNameIn(fishScientificNames);

		if (isTraceEnabled) {
			log.trace("######## End getFishesByParasiteScientificName ########");
		}

		return listFishByParasite;
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Busca un parásito en la base de datos o, si no existe, consulta WoRMS para obtener su información taxonómica
	 * y los hospedadores asociados. Luego lo guarda y lo retorna.
	 *
	 * @param scientificName Nombre científico del parásito.
	 * @return Optional que contiene el parásito guardado si se pudo obtener desde WoRMS.
	 */
	@Transactional
	public Optional<Parasite> findOrFetchAndPrepareParasite(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start findOrFetchAndPrepareParasite ########");
		}

		Optional<Parasite> optParasite = parasiteRepository.findByScientificName(scientificName);
		if (optParasite.isPresent()) {
			return optParasite;
		} else {
			log.trace("Llamando a WoRMS con nombre científico: {}", scientificName);
			Optional<ApiTaxonomyDTO> taxonomyOpt = bioDataClient.fetchFromWoRMS(scientificName);
			log.trace("Respuesta de WoRMS: {}", taxonomyOpt);
			if (taxonomyOpt.isPresent()) {
				ApiTaxonomyDTO taxonomyDto = taxonomyOpt.get();

				Taxonomy taxonomy = taxonomyMapper.convertApiTaxonomyDtoToTaxonomy(taxonomyDto);

				Parasite parasite = Parasite
						.builder()
						.scientificName(scientificName)
						.taxonomy(taxonomy)
						.build();

				List<String> hostIds = bioDataClient
						.fetchBiodataIdsByAphiaID(String.valueOf(taxonomyDto.getAphiaID()), "Host/prey");

				Set<Fish> hosts = hostIds
						.stream()
						.distinct()
						.map(id -> {
							ResponseEntity<String> response = bioDataClient.getScientificNameByAphiaID(id);
							if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
								return getOrCreateFish(response.getBody());
							}
							return null;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toSet());
				parasite.setFishes(hosts);

				Parasite saved = parasiteRepository.save(parasite);

				if (isTraceEnabled) {
					log.trace("######## End findOrFetchAndPrepareParasite ########");
				}

				return Optional.of(saved);
			} else {
				return Optional.empty();
			}
		}
	}

	/**
	 * Guarda un nuevo parásito en la base de datos con los datos proporcionados.
	 *
	 * @param createParasiteDTO DTO con la información del nuevo parásito.
	 * @return Entidad Parasite guardada.
	 */
	@Transactional
	public Parasite saveParasite(CreateParasiteDTO createParasiteDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start save Parasite ########");
		}

		Set<Fish> fishes = createParasiteDTO
				.getFishes()
				.stream()
				.map(this::getOrCreateFish)
				.collect(Collectors.toSet());

		Parasite parasite = parasiteMapper.convertCreateParasiteDtoToParasiteEntity(createParasiteDTO);
		parasite.setFishes(fishes);

	    Parasite savedParasite = parasiteRepository.save(parasite);

		if (isTraceEnabled) {
			log.trace("######## End save Parasite ########");
		}

		return savedParasite;
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualiza los datos de un parásito existente, identificado por su nombre científico.
	 * Solo se actualizan los campos modificados respecto al valor actual.
	 *
	 * @param scientificName Nombre científico del parásito a actualizar.
	 * @param updateParasiteDTO DTO con los nuevos valores para el parásito.
	 * @return Optional que contiene el parásito actualizado.
	 */
	@Transactional
	public Optional<Parasite> updateParasite(String scientificName, UpdateParasiteDTO updateParasiteDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start update Parasite ########");
		}

		Parasite parasiteDb = parasiteRepository
				.findByScientificName(scientificName)
				.orElseThrow(NoSuchElementException::new);

		AtomicBoolean updated = new AtomicBoolean(false);

		updateIfChangedAndValidate(updateParasiteDTO.getCommonName(), parasiteDb::getCommonName,
				parasiteDb::setCommonName, "commonName",
				parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getDiagnosticFeatures(), parasiteDb::getDiagnosticFeatures,
				parasiteDb::setDiagnosticFeatures, "diagnosticFeatures", parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getSize(), parasiteDb::getSize, parasiteDb::setSize, "size",
				parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getGeographicalDistribution(),
				parasiteDb::getGeographicalDistribution,
				parasiteDb::setGeographicalDistribution, "geographicalDistribution", parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getBiologicalCycle(), parasiteDb::getBiologicalCycle,
				parasiteDb::setBiologicalCycle,
				"biologicalCycle", parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getLocationOnHost(), parasiteDb::getLocationOnHost,
				parasiteDb::setLocationOnHost,
				"locationOnHost", parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getDamagesOnHost(), parasiteDb::getDamagesOnHost,
				parasiteDb::setDamagesOnHost,
				"damagesOnHost", parasiteDb, updated);
		updateIfChangedAndValidate(updateParasiteDTO.getDetection(), parasiteDb::getDetection, parasiteDb::setDetection,
				"detection",
				parasiteDb, updated);

		if (updateParasiteDTO.getTaxonomy() != null
				&& !Objects.equals(updateParasiteDTO.getTaxonomy(), parasiteDb.getTaxonomy())) {
			parasiteDb.setTaxonomy(updateParasiteDTO.getTaxonomy());
			updated.set(true);
		}

		if (updateParasiteDTO.getFishes() != null && !updateParasiteDTO.getFishes().isEmpty()) {
			Set<Fish> updatedFishes = updateParasiteDTO
					.getFishes()
					.stream()
					.map(this::getOrCreateFish)
					.collect(Collectors.toSet());
			parasiteDb.setFishes(updatedFishes);
			updated.set(true);
		}

		Parasite savedParasite = parasiteRepository.save(parasiteDb);

		if (isTraceEnabled) {
			log.trace("######## End update Parasite ########");
		}

		return Optional.of(savedParasite);
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Elimina un parásito del sistema a partir de su nombre científico.
	 *
	 * @param scientificName Nombre científico del parásito a eliminar.
	 * @return Entidad Parasite eliminada.
	 * @throws ParasiteNotFoundException si el parásito no existe.
	 */
	@Transactional
	public Parasite deleteParasite(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start deleteParasite ########");
		}

		Parasite parasite = parasiteRepository
				.findByScientificName(scientificName)
				.orElseThrow(ParasiteNotFoundException::new);

		parasiteRepository.delete(parasite);

		if (isTraceEnabled) {
			log.trace("######## End deleteParasite ########");
		}

		return parasite;
	}

	////////////////////////////
	//// MÉTODOS AUXILIARES ////
	////////////////////////////

	/**
	 * Actualiza un campo de la entidad {@link Parasite} si el nuevo valor es distinto al actual.
	 * Antes de realizar la actualización, valida el nuevo valor usando el validador de Bean Validation.
	 * 
	 * @param newValue     Nuevo valor propuesto para el campo.
	 * @param getter       Función que obtiene el valor actual del campo.
	 * @param setter       Función que establece el nuevo valor en la entidad.
	 * @param fieldName    Nombre del campo a validar (debe coincidir con el nombre de la propiedad en la clase).
	 * @param parasiteDb   Instancia de {@link Parasite} que está siendo actualizada.
	 * @param isUpdated    Indicador de si se ha producido alguna actualización en la entidad.
	 * @throws IllegalArgumentException si la validación del nuevo valor no se supera.
	 */
	private void updateIfChangedAndValidate(String newValue, Supplier<String> getter, Consumer<String> setter,
			String fieldName, Parasite parasiteDb, AtomicBoolean isUpdated) {
		Optional.ofNullable(newValue).filter(value -> !Objects.equals(value, getter.get())).ifPresent(value -> {
			Set<ConstraintViolation<Parasite>> violations = validator.validateValue(Parasite.class, fieldName, value);
			if (!violations.isEmpty()) {
				throw new IllegalArgumentException(violations.iterator().next().getMessage());
			}
			setter.accept(value);
			isUpdated.set(true);
		});
	}

	/**
	 * Recupera un pez por su nombre científico, y si no existe lo crea, enriqueciendo sus datos
	 * con información obtenida de fuentes externas como WoRMS.
	 *
	 * @param scientificName Nombre científico del pez.
	 * @return Entidad Fish existente o creada.
	 */
	private Fish getOrCreateFish(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start getOrCreateFish Parasite ########");
		}

		return fishRepository
				.findByScientificName(scientificName)
				.orElseGet(() -> {
			Optional<ApiFishDTO> apiFishOpt = bioDataClient
					.fetchBioData(scientificName)
					.map(data -> fishMapper.convertMapToApiFishDto(data));

			Fish fish;
			
			if (apiFishOpt.isPresent()) {
				fish = fishMapper.convertApiFishDtoToFishEntity(apiFishOpt.get());
			} else {
				fish = Fish.builder().scientificName(scientificName).build();
			}

			Optional<ApiTaxonomyDTO> taxonomyOpt = bioDataClient.fetchFromWoRMS(scientificName);
            taxonomyOpt.ifPresent(taxonomyDto -> {
				Taxonomy taxonomy = taxonomyMapper.convertApiTaxonomyDtoToTaxonomy(taxonomyOpt.get());
				fish.setTaxonomy(taxonomy);
            });

			if (isTraceEnabled) {
				log.trace("######## End getOrCreateFish Parasite ########");
			}

			return fishRepository.save(fish);
		});
	}

}
