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

import com.fish_diseases.treatment_service.dtos.CreateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.dtos.UpdateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.entities.LaboratoryMethod;
import com.fish_diseases.treatment_service.entities.Treatment;
import com.fish_diseases.treatment_service.exceptions.LaboratoryMethodNotFoundException;
import com.fish_diseases.treatment_service.mapper.LaboratoryMethodMapper;
import com.fish_diseases.treatment_service.repositories.LaboratoryMethodRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servicio para gestionar las operaciones relacionadas con los métodos de laboratorio.
 * Proporciona funcionalidades CRUD y validación de campos.
 */
@Service
public class LaboratoryMethodService {

	private static final Logger log = LoggerFactory.getLogger(Treatment.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private LaboratoryMethodRepository laboratoryMethodRepository;
	private LaboratoryMethodMapper laboratoryMethodMapper;
	private Validator validator;

	/**
	 * Constructor de {@code LaboratoryMethodService}.
	 *
	 * @param laboratoryMethodRepository repositorio para acceder a los métodos de laboratorio
	 * @param laboratoryMethodMapper mapper para convertir entre entidades y DTOs de método de laboratorio
	 * @param validator validador para aplicar reglas de validación a los campos de la entidad
	 */
	public LaboratoryMethodService(LaboratoryMethodRepository laboratoryMethodRepository,
			LaboratoryMethodMapper laboratoryMethodMapper, Validator validator) {
		super();
		this.laboratoryMethodRepository = laboratoryMethodRepository;
		this.laboratoryMethodMapper = laboratoryMethodMapper;
		this.validator = validator;
	}

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Obtiene todos los métodos de laboratorio registrados en el sistema.
	 *
	 * @return lista de todos los métodos de laboratorio
	 */
	@Transactional(readOnly = true)
	public List<LaboratoryMethod> findAll() {

		if (isTraceEnabled) {
			log.trace("######## Start findAll LaboratoryMethod ########");
		}

		List<LaboratoryMethod> allLaboratoryMethods = laboratoryMethodRepository.findAll();

		if (isTraceEnabled) {
			log.trace("######## End findAll LaboratoryMethod ########");
		}

		return allLaboratoryMethods;
	}

	/**
	 * Busca un método de laboratorio por su identificador único.
	 *
	 * @param id identificador del método de laboratorio
	 * @return entidad {@code LaboratoryMethod} correspondiente
	 * @throws LaboratoryMethodNotFoundException si no se encuentra el método
	 */
	@Transactional(readOnly = true)
	public LaboratoryMethod findById(int id) {

		if (isTraceEnabled) {
			log.trace("######## Start findById LaboratoryMethod ########");
		}

		LaboratoryMethod laboratoryMethod = laboratoryMethodRepository
				.findById(id)
				.orElseThrow(LaboratoryMethodNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findById LaboratoryMethod ########");
		}

		return laboratoryMethod;
	}

	/**
	 * Busca un método de laboratorio por su nombre.
	 *
	 * @param laboratoryMethodName nombre del método de laboratorio
	 * @return entidad {@code LaboratoryMethod} correspondiente
	 * @throws LaboratoryMethodNotFoundException si no se encuentra el método
	 */
	@Transactional(readOnly = true)
	public LaboratoryMethod findByName(String laboratoryMethodName) {

		if (isTraceEnabled) {
			log.trace("######## Start findByName LaboratoryMethod ########");
		}

		LaboratoryMethod laboratoryMethod = laboratoryMethodRepository
				.findByName(laboratoryMethodName)
				.orElseThrow(LaboratoryMethodNotFoundException::new);

		if (isTraceEnabled) {
			log.trace("######## End findByName LaboratoryMethod ########");
		}

		return laboratoryMethod;
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Guarda un nuevo método de laboratorio en la base de datos.
	 *
	 * @param createLaboratoryMethodDTO DTO con los datos del nuevo método de laboratorio
	 * @return entidad guardada {@code LaboratoryMethod}
	 */
	@Transactional
	public LaboratoryMethod save(CreateLaboratoryMethodDTO createLaboratoryMethodDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start save LaboratoryMethod ########");
		}

		LaboratoryMethod laboratoryMethod = laboratoryMethodMapper.convertCreateLaboratoryMethodDtoToLaboratoryMethodEntity(createLaboratoryMethodDTO);

		if (isTraceEnabled) {
			log.trace("######## End save LaboratoryMethod ########");
		}

		return laboratoryMethodRepository.save(laboratoryMethod);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualiza los datos de un método de laboratorio existente.
	 *
	 * @param id identificador del método de laboratorio a actualizar
	 * @param updateLaboratoryMethodDTO DTO con los datos a actualizar
	 * @return {@code Optional<LaboratoryMethod>} con la entidad actualizada
	 * @throws LaboratoryMethodNotFoundException si el método no existe
	 */
	@Transactional
	public Optional<LaboratoryMethod> update(int id, UpdateLaboratoryMethodDTO updateLaboratoryMethodDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start update LaboratoryMethod ########");
		}

		LaboratoryMethod laboratoryMethodDb = laboratoryMethodRepository.findById(id)
				.orElseThrow(LaboratoryMethodNotFoundException::new);

		AtomicBoolean isUpdated = new AtomicBoolean(false);

		// Validar y actualizar solo los campos modificados
		updateIfChangedAndValidate(updateLaboratoryMethodDTO.getTechnique(), laboratoryMethodDb::getTechnique,
				laboratoryMethodDb::setTechnique, "technique", laboratoryMethodDb, isUpdated);

		if (isUpdated.get()) {
			laboratoryMethodRepository.updateUpdatedAt(id, LocalDateTime.now());
		}

		if (isTraceEnabled) {
			log.trace("######## End update LaboratoryMethod ########");
		}

		return Optional.of(laboratoryMethodRepository.save(laboratoryMethodDb));
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Elimina un método de laboratorio de la base de datos.
	 *
	 * @param id identificador del método de laboratorio a eliminar
	 * @return entidad eliminada {@code LaboratoryMethod}
	 * @throws LaboratoryMethodNotFoundException si el método no existe
	 */
	@Transactional
	public LaboratoryMethod deleteLaboratoryMethod(int id) {

		if (isTraceEnabled) {
			log.trace("######## Start deleteLaboratoryMethod ########");
		}

		LaboratoryMethod laboratoryMethodDb = laboratoryMethodRepository
				.findById(id)
				.orElseThrow(LaboratoryMethodNotFoundException::new);

		laboratoryMethodRepository.deleteById(laboratoryMethodDb.getId());

		if (isTraceEnabled) {
			log.trace("######## End deleteLaboratoryMethod ########");
		}

		return laboratoryMethodDb;
	}

	////////////////////////////
	//// MÉTODOS AUXILIARES ////
	////////////////////////////
	
	/**
	 * Valida y actualiza un campo si el nuevo valor es distinto del actual.
	 * Si el valor ha cambiado y es válido según las restricciones de la clase,
	 * se aplica el cambio y se marca que hubo actualización.
	 *
	 * @param newValue nuevo valor que se desea establecer
	 * @param getter proveedor del valor actual
	 * @param setter consumidor que aplica el nuevo valor
	 * @param fieldName nombre del campo a validar (coincide con el nombre de la propiedad en la entidad)
	 * @param laboratoryMethodDb instancia de {@code LaboratoryMethod} que se está actualizando
	 * @param isUpdated bandera que indica si algún campo fue modificado
	 * @throws IllegalArgumentException si el nuevo valor viola las restricciones de validación
	 */
	private void updateIfChangedAndValidate(String newValue, Supplier<String> getter, Consumer<String> setter,
			String fieldName, LaboratoryMethod laboratoryMethodDb, AtomicBoolean isUpdated) {
		if (newValue != null && !Objects.equals(newValue, getter.get())) {
		    Set<ConstraintViolation<LaboratoryMethod>> violations = validator.validateValue(LaboratoryMethod.class, fieldName, newValue);
		    if (!violations.isEmpty()) {
		        throw new IllegalArgumentException(violations.iterator().next().getMessage());
		    }
		    setter.accept(newValue);
		    isUpdated.set(true);
		}
	}

}
