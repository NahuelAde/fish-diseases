package com.fish_diseases.biodata_service.controllers;

import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.BAD_REQUEST_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.BAD_REQUEST_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.CONFLICT_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.CONFLICT_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.CREATED_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.CREATED_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.NOT_FOUND_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.NOT_FOUND_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.NO_CONTENT_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.NO_CONTENT_DESCRIPTION;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.OK_CODE;
import static com.fish_diseases.biodata_service.featurehelper.SwaggerConstants.OK_DESCRIPTION;

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

import com.fish_diseases.biodata_service.dtos.CreateParasiteDTO;
import com.fish_diseases.biodata_service.dtos.UpdateParasiteDTO;
import com.fish_diseases.biodata_service.entities.Parasite;
import com.fish_diseases.biodata_service.services.ParasiteService;
import com.fish_diseases.biodata_service.utils.ResponseUtil;
import com.fish_diseases.biodata_service.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/parasites")
@Tag(name = "Parásitos", description = "Descripción taxonómica de los parásitos.")
public class ParasiteController {

	@Autowired
	private ParasiteService parasiteService;

	@Autowired
	private ResponseUtil responseUtil;

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Devuelve todos los parásitos
	 * 
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping
	public ResponseEntity<?> list() {
		List<Parasite> parasites = parasiteService.findAll();
		if (parasites.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(parasites);
	}

	/**
	 * Devuelve parásito por Id
	 * 
	 * @param id del parásito
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/{id}")
	public ResponseEntity<?> getParasiteById(@PathVariable Long id) {
		Parasite parasite = parasiteService.findById(id);
		return ResponseEntity.ok(parasite);
	}

	/**
	 * Devuelve parásito por scientificName
	 * 
	 * @param scientificName del parásito
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/sn/{scientificName}")
	public ResponseEntity<?> getParasiteByScientificName(@PathVariable String scientificName) {
		Parasite parasite = parasiteService.findByScientificName(scientificName);
		return ResponseEntity.ok(parasite);
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Obtiene datos de parásito desde API externa por scientificName
	 * 
	 * @param scientificName del parásito
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/fetch/{scientificName}")
	public ResponseEntity<?> fetchParasiteData(@PathVariable String scientificName) {
		return parasiteService
				.findOrFetchAndPrepareParasite(scientificName)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElseGet(() -> responseUtil.error("error.parasiteNotFound", HttpStatus.NOT_FOUND));
	}

	/**
	 * Crea nuevo parásito en la base de datos
	 * 
	 * @param createParasiteDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = CREATED_CODE, description = CREATED_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping
	public ResponseEntity<?> createParasite(@Valid @RequestBody CreateParasiteDTO createParasiteDTO,
			BindingResult result) {

		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		parasiteService.saveParasite(createParasiteDTO);
		return responseUtil.success("message.parasiteCreated", HttpStatus.CREATED);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualizar parásito por scientificName
	 * 
	 * @param scientificName    de parásito
	 * @param updateParasiteDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PatchMapping("/{scientificName}")
	public ResponseEntity<?> updateParasite(@PathVariable String scientificName,
			@RequestBody @Valid UpdateParasiteDTO updateParasiteDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		parasiteService.findByScientificName(scientificName);

		return parasiteService
				.updateParasite(scientificName, updateParasiteDTO)
				.map(updatedParasite -> responseUtil.success("message.parasiteUpdated", HttpStatus.OK))
				.orElseGet(() -> responseUtil.error("error.parasiteNotUpdated", HttpStatus.NO_CONTENT));
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Eliminar parásito por scientificName
	 * 
	 * @param scientificName de parásito
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Parasite.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@DeleteMapping("/{scientificName}")
	public ResponseEntity<?> deleteParasite(@PathVariable String scientificName) {
		parasiteService.deleteParasite(scientificName);
		return responseUtil.success("message.parasiteDeleted", HttpStatus.OK);

	}

}
