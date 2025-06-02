package com.fish_diseases.treatment_service.controllers;

import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.BAD_REQUEST_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.BAD_REQUEST_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.CONFLICT_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.CONFLICT_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.CREATED_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.CREATED_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.NOT_FOUND_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.NOT_FOUND_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.NO_CONTENT_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.NO_CONTENT_DESCRIPTION;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.OK_CODE;
import static com.fish_diseases.treatment_service.featurehelper.SwaggerConstants.OK_DESCRIPTION;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fish_diseases.treatment_service.dtos.CreateTreatmentDTO;
import com.fish_diseases.treatment_service.dtos.UpdateTreatmentDTO;
import com.fish_diseases.treatment_service.entities.Treatment;
import com.fish_diseases.treatment_service.services.TreatmentService;
import com.fish_diseases.treatment_service.utils.ResponseUtil;
import com.fish_diseases.treatment_service.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/treatments")
@Tag(name = "Tratamientos", description = "Descripción de los tratamientos en parasitosis de los peces.")
public class TreatmentController {

	@Autowired
	private TreatmentService treatmentService;

	@Autowired
	private ResponseUtil responseUtil;

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Devuelve todos los tratamientos
	 * 
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping
	public ResponseEntity<?> list() {
		List<Treatment> treatments = treatmentService.findAll();
		if (treatments.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(treatments);
	}

	/**
	 * Devuelve tratamiento por Id
	 * 
	 * @param id
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/{id}")
	public ResponseEntity<?> getTreatmentById(@PathVariable int id) {
		Treatment treatment = treatmentService.findById(id);
		return ResponseEntity.ok(treatment);
	}

	/**
	 * Devuelve tratamiento por nombre
	 * 
	 * @param treatmentName
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/name/{treatmentName}")
	public ResponseEntity<?> getTreatmentByName(@PathVariable String treatmentName) {
		Treatment treatment = treatmentService.findByName(treatmentName);
		return ResponseEntity.ok(treatment);
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Crea nuevo tratamiento en la base de datos
	 * 
	 * @param createTreatmentDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = CREATED_CODE, description = CREATED_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping
	public ResponseEntity<?> createTreatment(@Valid @RequestBody CreateTreatmentDTO createTreatmentDTO,
			BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		treatmentService.save(createTreatmentDTO);
		return responseUtil.success("message.treatmentCreated", HttpStatus.CREATED);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualizar tratamiento por Id
	 * 
	 * @param id
	 * @param updateTreatmentDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateTreatment(@PathVariable int id,
			@RequestBody @Valid UpdateTreatmentDTO updateTreatmentDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		treatmentService.findById(id);

		return treatmentService
				.update(id, updateTreatmentDTO)
				.map(updatedTreatment -> responseUtil.success("message.treatmentUpdated", HttpStatus.OK))
				.orElseGet(() -> responseUtil.error("error.treatmentNotUpdated", HttpStatus.BAD_REQUEST));
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Eliminar tratamiento por Id
	 * 
	 * @param id
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Treatment.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTreatment(@PathVariable int id) {
		treatmentService.deleteTreatment(id);
		return responseUtil.success("message.treatmentDeleted", HttpStatus.OK);
	}

}