package com.fish_diseases.treatment_service.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fish_diseases.treatment_service.dtos.CreateTreatmentDTO;
import com.fish_diseases.treatment_service.dtos.UpdateTreatmentDTO;
import com.fish_diseases.treatment_service.entities.Treatment;
import com.fish_diseases.treatment_service.exceptions.TreatmentNotFoundException;
import com.fish_diseases.treatment_service.mapper.TreatmentMapper;
import com.fish_diseases.treatment_service.repositories.TreatmentRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servicio para gestionar operaciones CRUD y de negocio relacionadas con tratamientos.
 */
@Service
public class TreatmentService {

	private static final Logger log = LoggerFactory.getLogger(Treatment.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private TreatmentRepository treatmentRepository;
	private TreatmentMapper treatmentMapper;
	private Validator validator;

	/**
	 * Constructor que inyecta las dependencias necesarias.
	 *
	 * @param treatmentRepository repositorio de tratamientos
	 * @param treatmentMapper     mapper para convertir DTOs a entidades
	 * @param validator           validador de campos
	 */
	public TreatmentService(TreatmentRepository treatmentRepository, TreatmentMapper treatmentMapper,
			Validator validator) {
		super();
		this.treatmentRepository = treatmentRepository;
		this.treatmentMapper = treatmentMapper;
		this.validator = validator;
	}

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Recupera todos los tratamientos almacenados.
	 *
	 * @return lista de tratamientos
	 */
	@Transactional(readOnly = true)
	public List<Treatment> findAll() {

		if (isTraceEnabled) {
			log.trace("######## Start findAll Treatment ########");
		}

		List<Treatment> allTreatments = treatmentRepository.findAll();

		if (isTraceEnabled) {
			log.trace("######## End findAll Treatment ########");
		}

		return allTreatments;
	}

	/**
	 * Busca un tratamiento por su ID.
	 *
	 * @param id identificador del tratamiento
	 * @return tratamiento encontrado
	 * @throws TreatmentNotFoundException si no se encuentra el tratamiento
	 */
	@Transactional(readOnly = true)
	public Treatment findById(int id) {

		if (isTraceEnabled) {
			log.trace("######## Start findById Treatment ########");
		}

		Treatment treatment = treatmentRepository
				.findById(id)
				.orElseThrow(TreatmentNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findById Treatment ########");
		}

		return treatment;
	}

	/**
	 * Busca un tratamiento por su nombre.
	 *
	 * @param treatmentName nombre del tratamiento
	 * @return tratamiento encontrado
	 * @throws TreatmentNotFoundException si no se encuentra el tratamiento
	 */
	@Transactional(readOnly = true)
	public Treatment findByName(String treatmentName) {

		if (isTraceEnabled) {
			log.trace("######## Start findByName Treatment ########");
		}

		Treatment treatment = treatmentRepository
				.findByName(treatmentName)
				.orElseThrow(TreatmentNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findByName Treatment ########");
		}

		return treatment;
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Guarda un nuevo tratamiento a partir de un DTO.
	 *
	 * @param createTreatmentDTO datos del tratamiento a crear
	 * @return tratamiento creado y persistido
	 */
	@Transactional
	public Treatment save(CreateTreatmentDTO createTreatmentDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start save Treatment ########");
		}

		Treatment treatment = treatmentMapper.convertCreateTreatmentDtoToTreatmentEntity(createTreatmentDTO);

		if (isTraceEnabled) {
			log.trace("######## End save Treatment ########");
		}

		return treatmentRepository.save(treatment);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualiza un tratamiento existente por su ID con los datos proporcionados.
	 *
	 * @param id                 ID del tratamiento a actualizar
	 * @param updateTreatmentDTO DTO con los nuevos datos
	 * @return tratamiento actualizado (envuelto en Optional)
	 * @throws TreatmentNotFoundException si no se encuentra el tratamiento
	 */
	@Transactional
	public Optional<Treatment> update(int id, UpdateTreatmentDTO updateTreatmentDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start update Treatment ########");
		}

		Treatment treatmentDb = treatmentRepository
				.findById(id)
				.orElseThrow(TreatmentNotFoundException::new);

		AtomicBoolean isUpdated = new AtomicBoolean(false);

		updateIfChangedAndValidate(updateTreatmentDTO.getDescription(), treatmentDb::getDescription,
				treatmentDb::setDescription, "description", treatmentDb, isUpdated);
		updateIfChangedAndValidate(updateTreatmentDTO.getActionSpectrum(), treatmentDb::getActionSpectrum,
				treatmentDb::setActionSpectrum, "actionSpectrum", treatmentDb, isUpdated);
		updateIfChangedAndValidate(updateTreatmentDTO.getDose(), treatmentDb::getDose, treatmentDb::setDose, "dose",
				treatmentDb, isUpdated);

		if (isUpdated.get()) {
			treatmentRepository.updateUpdatedAt(id, LocalDateTime.now());
		}

		Treatment savedTreatment = treatmentRepository.save(treatmentDb);

		if (isTraceEnabled) {
			log.trace("######## End update Treatment ########");
		}

		return Optional.of(savedTreatment);
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Elimina un tratamiento por su ID.
	 *
	 * @param id identificador del tratamiento a eliminar
	 * @return tratamiento eliminado
	 * @throws TreatmentNotFoundException si no se encuentra el tratamiento
	 */
	@Transactional
	public Treatment deleteTreatment(int id) {

		if (isTraceEnabled) {
			log.trace("######## Start deleteTreatment ########");
		}

		Treatment treatment = treatmentRepository
				.findById(id)
				.orElseThrow(TreatmentNotFoundException::new);

		treatmentRepository.deleteById(id);

		if (isTraceEnabled) {
			log.trace("######## End deleteTreatment ########");
		}

		return treatment;
	}

	////////////////////////////
	//// MÉTODOS AUXILIARES ////
	////////////////////////////

	/**
	 * Valida y actualiza un campo si el nuevo valor es distinto del actual.
	 * Si el valor ha cambiado y es válido según las restricciones de la clase,
	 * se aplica el cambio y se marca que hubo actualización.
	 *
	 * @param newValue   nuevo valor del campo
	 * @param getter     función para obtener el valor actual
	 * @param setter     función para establecer el nuevo valor
	 * @param fieldName  nombre del campo (para validación)
	 * @param treatmentDb entidad a modificar
	 * @param isUpdated  indicador de si se ha actualizado algún campo
	 */
	private void updateIfChangedAndValidate(String newValue, Supplier<String> getter, Consumer<String> setter,
			String fieldName, Treatment treatmentDb, AtomicBoolean isUpdated) {
		if (newValue != null && !Objects.equals(newValue, getter.get())) {
			Set<ConstraintViolation<Treatment>> violations = validator
					.validateValue(Treatment.class, fieldName, newValue);
			if (!violations.isEmpty()) {
				throw new IllegalArgumentException(violations.iterator().next().getMessage());
			}
			setter.accept(newValue);
			isUpdated.set(true);
		}
	}

}
