package com.fish_diseases.biodata_service.services;

import java.util.HashSet;
import java.util.List;
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
import com.fish_diseases.biodata_service.dtos.ApiParasiteDTO;
import com.fish_diseases.biodata_service.dtos.ApiTaxonomyDTO;
import com.fish_diseases.biodata_service.dtos.CreateFishDTO;
import com.fish_diseases.biodata_service.dtos.UpdateFishDTO;
import com.fish_diseases.biodata_service.entities.Fish;
import com.fish_diseases.biodata_service.entities.Parasite;
import com.fish_diseases.biodata_service.entities.Taxonomy;
import com.fish_diseases.biodata_service.exceptions.FishNotFoundException;
import com.fish_diseases.biodata_service.mapper.FishMapper;
import com.fish_diseases.biodata_service.mapper.ParasiteMapper;
import com.fish_diseases.biodata_service.mapper.TaxonomyMapper;
import com.fish_diseases.biodata_service.repositories.FishRepository;
import com.fish_diseases.biodata_service.repositories.ParasiteRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servicio para la gestión de peces (huéspedes) en el sistema.
 * Incluye operaciones de consulta, creación, actualización y eliminación de peces,
 * así como métodos auxiliares para integración con fuentes externas como WoRMS.
 */
@Service
public class FishService {

	private static final Logger log = LoggerFactory.getLogger(FishService.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private FishRepository fishRepository;
	private ParasiteRepository parasiteRepository;
	private BioDataClient bioDataClient;
	private FishMapper fishMapper;
	private ParasiteMapper parasiteMapper;
	private TaxonomyMapper taxonomyMapper;
	private Validator validator;

	/**
	 * Constructor de {@code FishService} que inicializa todas las dependencias requeridas.
	 *
	 * @param fishRepository repositorio para acceder a datos de peces
	 * @param parasiteRepository repositorio para acceder a datos de parásitos
	 * @param bioDataClient cliente para comunicarse con el servicio de datos biológicos
	 * @param fishMapper mapper para transformar entidades de peces y sus DTOs
	 * @param parasiteMapper mapper para transformar entidades de parásitos y sus DTOs
	 * @param taxonomyMapper mapper para convertir información taxonómica
	 * @param validator validador para aplicar reglas de validación a los datos
	 */
	public FishService(FishRepository fishRepository, ParasiteRepository parasiteRepository,
			BioDataClient bioDataClient, FishMapper fishMapper, ParasiteMapper parasiteMapper,
			TaxonomyMapper taxonomyMapper, Validator validator) {
		super();
		this.fishRepository = fishRepository;
		this.parasiteRepository = parasiteRepository;
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
	 * Recupera todos los peces registrados en la base de datos.
	 *
	 * @return Lista de entidades {@link Fish}.
	 */
	@Transactional(readOnly = true)
	public List<Fish> findAll() {

		if (isTraceEnabled) {
			log.trace("######## Start findAll Fish ########");
		}

		List<Fish> allFishes = fishRepository.findAll();

		if (isTraceEnabled) {
			log.trace("######## End findAll Fish ########");
		}

		return allFishes;
	}

	/**
	 * Busca un pez por su identificador único.
	 *
	 * @param id ID del pez.
	 * @return Entidad {@link Fish} correspondiente.
	 * @throws FishNotFoundException si no se encuentra el pez.
	 */
	@Transactional(readOnly = true)
	public Fish findById(Long id) {

		if (isTraceEnabled) {
			log.trace("######## Start findById Fish ########");
		}

		Fish fish = fishRepository
				.findById(id)
				.orElseThrow(FishNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findById Fish ########");
		}

		return fish;
	}

	/**
	 * Busca un pez por su nombre científico.
	 *
	 * @param scientificName Nombre científico del pez.
	 * @return Entidad {@link Fish} correspondiente.
	 * @throws FishNotFoundException si no se encuentra el pez.
	 */
	@Transactional(readOnly = true)
	public Fish findByScientificName(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start findByScientificName Fish ########");
		}

		Fish fish = fishRepository
				.findByScientificName(scientificName)
				.orElseThrow(FishNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findByScientificName Fish ########");
		}

		return fish;
	}

	/**
	 * Recupera los parásitos asociados a un pez identificado por su nombre científico.
	 *
	 * @param fishScientificName Nombre científico del pez.
	 * @return Lista de entidades {@link Parasite} asociadas al pez.
	 */
	@Transactional(readOnly = true)
	public List<Parasite> getParasitesByFishScientificName(String fishScientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start getParasitesByFishScientificName ########");
		}

		Fish fish = findByScientificName(fishScientificName);
		
		List<String> parasiteScientificNames = fish
				.getParasites()
				.stream()
				.map(Parasite::getScientificName)
				.collect(Collectors.toList());
		List<Parasite> listParasiteByFish = parasiteRepository.findByScientificNameIn(parasiteScientificNames);

		if (isTraceEnabled) {
			log.trace("######## Start getParasitesByFishScientificName ########");
		}

		return listParasiteByFish;
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Busca un pez en la base de datos por nombre científico. Si no existe, obtiene datos
	 * de WoRMS, crea el pez con su información taxonómica y parásitos asociados,
	 * lo guarda y lo retorna.
	 *
	 * @param scientificName Nombre científico del pez.
	 * @return {@link Optional} con el pez creado o encontrado.
	 */
	@Transactional
	public Optional<Fish> findOrFetchAndPrepareFish(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start findOrFetchAndPrepareFish ########");
		}

		Optional<Fish> optFish = fishRepository.findByScientificName(scientificName);
		if (optFish.isPresent()) {
			return optFish;
		} else {
			Optional<ApiTaxonomyDTO> taxonomyOpt = bioDataClient.fetchFromWoRMS(scientificName);
			if (taxonomyOpt.isPresent()) {
				ApiTaxonomyDTO taxonomyDto = taxonomyOpt.get();

				Taxonomy taxonomy = taxonomyMapper.convertApiTaxonomyDtoToTaxonomy(taxonomyDto);

				Fish fish = Fish
						.builder()
						.scientificName(scientificName)
						.taxonomy(taxonomy)
						.build();

				List<String> parasiteIds = bioDataClient
						.fetchBiodataIdsByAphiaID(String.valueOf(taxonomyDto.getAphiaID()), "Host/prey");

				Set<Parasite> parasites = parasiteIds
						.stream()
						.distinct()
						.map(id -> {
							ResponseEntity<String> response = bioDataClient.getScientificNameByAphiaID(id);
							if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
								return getOrCreateParasite(response.getBody());
							}
							return null;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toSet());

				fish.setParasites(parasites);

				Fish saved = fishRepository.save(fish);

				if (isTraceEnabled) {
					log.trace("######## End findOrFetchAndPrepareFish ########");
				}

				return Optional.of(saved);
			} else {
				return Optional.empty();
			}
		}
	}

	/**
	 * Guarda un nuevo pez en la base de datos con los parásitos asociados, si existen.
	 *
	 * @param createFishDTO DTO con los datos necesarios para crear el pez.
	 * @return El {@link Fish} guardado.
	 */
	@Transactional
	public Fish saveFish(CreateFishDTO createFishDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start save Fish ########");
		}

		Set<Parasite> parasites = createFishDTO
				.getParasites()
				.stream()
				.map(this::getOrCreateParasite)
				.collect(Collectors.toSet());

		Fish fish = fishMapper.convertCreateFishDtoToFishEntity(createFishDTO);
		fish.setParasites(parasites);
		
		Fish savedFish = fishRepository.save(fish);

		if (isTraceEnabled) {
			log.trace("######## End save Fish ########");
		}

		return savedFish;
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualiza los datos de un pez existente si hay cambios respecto a los nuevos valores.
	 * También actualiza los parásitos y la taxonomía, si se proporcionan.
	 *
	 * @param scientificName Nombre científico del pez a actualizar.
	 * @param updateFishDTO DTO con los nuevos datos del pez.
	 * @return {@link Optional} con el pez actualizado.
	 * @throws FishNotFoundException si no se encuentra el pez.
	 */
	@Transactional
	public Optional<Fish> updateFish(String scientificName, UpdateFishDTO updateFishDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start update Fish ########");
		}

		Fish fishDb = fishRepository
				.findByScientificName(scientificName)
				.orElseThrow(FishNotFoundException::new);

		if (fishDb.getParasites() == null) {
			fishDb.setParasites(new HashSet<>());
		}

		AtomicBoolean isUpdated = new AtomicBoolean(false);

		updateIfChangedAndValidate(updateFishDTO.getCommonName(), fishDb::getCommonName, fishDb::setCommonName,
				"commonName", fishDb, isUpdated);
		updateIfChangedAndValidate(updateFishDTO.getFaoDistribution(), fishDb::getFaoDistribution,
				fishDb::setFaoDistribution, "faoDistribution", fishDb, isUpdated);

		if (updateFishDTO.getTaxonomy() != null && !Objects.equals(updateFishDTO.getTaxonomy(), fishDb.getTaxonomy())) {
			fishDb.setTaxonomy(updateFishDTO.getTaxonomy());
			isUpdated.set(true);
		}

		if (updateFishDTO.getParasites() != null && !updateFishDTO.getParasites().isEmpty()) {
			Set<Parasite> updatedParasites = updateFishDTO
					.getParasites()
					.stream()
					.map(this::getOrCreateParasite)
					.collect(Collectors.toSet());
			fishDb.setParasites(updatedParasites);
			isUpdated.set(true);
		}

		Fish savedFish = fishRepository.save(fishDb);

		if (isTraceEnabled) {
			log.trace("######## End update Fish ########");
		}

		return Optional.of(savedFish);
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Elimina un pez de la base de datos según su nombre científico.
	 *
	 * @param scientificName Nombre científico del pez.
	 * @return El {@link Fish} eliminado.
	 * @throws FishNotFoundException si no se encuentra el pez.
	 */
	@Transactional
	public Fish deleteFish(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start deleteFish ########");
		}

		Fish fish = fishRepository
				.findByScientificName(scientificName)
				.orElseThrow(FishNotFoundException::new);

		fishRepository.delete(fish);

		if (isTraceEnabled) {
			log.trace("######## End deleteFish ########");
		}

		return fish;

	}

	////////////////////////////
	//// MÉTODOS AUXILIARES ////
	////////////////////////////

	/**
	 * Actualiza el valor de un campo si ha cambiado, y valida el nuevo valor según las
	 * restricciones de Bean Validation. Marca el estado como actualizado si se realiza el cambio.
	 *
	 * @param newValue Nuevo valor del campo.
	 * @param getter Función para obtener el valor actual del campo.
	 * @param setter Función para asignar el nuevo valor.
	 * @param fieldName Nombre del campo a validar.
	 * @param fishDb Entidad {@link Fish} a actualizar.
	 * @param isUpdated Bandera para indicar si hubo alguna actualización.
	 * @throws IllegalArgumentException si la validación del nuevo valor falla.
	 */
	private void updateIfChangedAndValidate(String newValue, Supplier<String> getter, Consumer<String> setter,
			String fieldName, Fish fishDb, AtomicBoolean isUpdated) {
		if (newValue != null && !Objects.equals(newValue, getter.get())) {
			Set<ConstraintViolation<Fish>> violations = validator.validateValue(Fish.class, fieldName, newValue);
			if (!violations.isEmpty()) {
				throw new IllegalArgumentException(violations.iterator().next().getMessage());
			}
			setter.accept(newValue);
			isUpdated.set(true);
		}
	}

	/**
	 * Busca un parásito por nombre científico. Si no existe, lo crea utilizando datos externos.
	 * Si es posible, se le asocia una entidad {@link Taxonomy}.
	 *
	 * @param scientificName Nombre científico del parásito.
	 * @return Instancia persistida de {@link Parasite}.
	 */
	private Parasite getOrCreateParasite(String scientificName) {

		if (isTraceEnabled) {
			log.trace("######## Start getOrCreateParasite Fish ########");
		}

		return parasiteRepository
				.findByScientificName(scientificName)
				.orElseGet(() -> {
					Optional<ApiParasiteDTO> apiParasiteOpt = bioDataClient
							.fetchBioData(scientificName)
							.map(data -> parasiteMapper.convertMapToApiParasiteDto(data));
					
					Parasite parasite;
					
					if (apiParasiteOpt.isPresent()) {
						parasite = parasiteMapper.convertApiParasiteDtoToParasiteEntity(apiParasiteOpt.get());
					} else {
							parasite = Parasite.builder().scientificName(scientificName).build();
					}
					
					Optional<ApiTaxonomyDTO> taxonomyOpt = bioDataClient.fetchFromWoRMS(scientificName);
		            taxonomyOpt.ifPresent(taxonomyDto -> {
						Taxonomy taxonomy = taxonomyMapper.convertApiTaxonomyDtoToTaxonomy(taxonomyOpt.get());
						parasite.setTaxonomy(taxonomy);
		            });

					if (isTraceEnabled) {
						log.trace("######## End getOrCreateParasite Fish ########");
					}

					return parasiteRepository.save(parasite);
				});
	}
}
