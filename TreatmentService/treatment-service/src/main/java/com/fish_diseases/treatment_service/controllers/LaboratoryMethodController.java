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

import com.fish_diseases.treatment_service.dtos.CreateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.dtos.UpdateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.entities.LaboratoryMethod;
import com.fish_diseases.treatment_service.services.LaboratoryMethodService;
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
@RequestMapping("/laboratory-methods")
@Tag(name = "Métodos de laboratorio", description = "Descripción de los métodos de laboratorio.")
public class LaboratoryMethodController {

	@Autowired
	private LaboratoryMethodService laboratoryMethodService;

	@Autowired
	private ResponseUtil responseUtil;

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Devuelve todos los métodos de laboratorio
	 * 
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping
	public ResponseEntity<?> list() {
		List<LaboratoryMethod> laboratoryMethods = laboratoryMethodService.findAll();
		if (laboratoryMethods.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(laboratoryMethods);
	}

	/**
	 * Devuelve métodos de laboratorio por Id
	 * 
	 * @param id
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/{id}")
	public ResponseEntity<?> getLaboratoryMethodById(@PathVariable int id) {
		LaboratoryMethod laboratoryMethod = laboratoryMethodService.findById(id);
		return ResponseEntity.ok(laboratoryMethod);
	}

	/**
	 * Devuelve métodos de laboratorio por nombre
	 * 
	 * @param laboratoryMethodName
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/name/{laboratoryMethodName}")
	public ResponseEntity<?> getLaboratoryMethodByName(@PathVariable String laboratoryMethodName) {
		LaboratoryMethod laboratoryMethod = laboratoryMethodService.findByName(laboratoryMethodName);
		return ResponseEntity.ok(laboratoryMethod);
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Crea nuevo métodos de laboratorio en la base de datos
	 * 
	 * @param createLaboratoryMethodDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = CREATED_CODE, description = CREATED_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping
	public ResponseEntity<?> createLaboratoryMethod(
			@Valid @RequestBody CreateLaboratoryMethodDTO createLaboratoryMethodDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		laboratoryMethodService.save(createLaboratoryMethodDTO);
		return responseUtil.success("message.laboratoryMethodCreated", HttpStatus.CREATED);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualizar métodos de laboratorio por Id
	 * 
	 * @param id
	 * @param updateLaboratoryMethodDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateLaboratoryMethod(@PathVariable int id,
			@RequestBody @Valid UpdateLaboratoryMethodDTO updateLaboratoryMethodDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		laboratoryMethodService.findById(id);

		return laboratoryMethodService
				.update(id, updateLaboratoryMethodDTO)
				.map(updatedLaboratoryMethod -> responseUtil.success("message.laboratoryMethodUpdated", HttpStatus.OK))
				.orElseGet(() -> responseUtil.error("error.laboratoryMethodNotUpdated", HttpStatus.BAD_REQUEST));
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Eliminar métodos de laboratorio por Id
	 * 
	 * @param id
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = LaboratoryMethod.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteLaboratoryMethod(@PathVariable int id) {
		laboratoryMethodService.deleteLaboratoryMethod(id);
		return responseUtil.success("message.laboratoryMethodDeleted", HttpStatus.OK);
	}

}
