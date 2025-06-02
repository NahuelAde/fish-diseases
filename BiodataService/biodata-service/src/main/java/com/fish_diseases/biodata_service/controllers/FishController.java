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

import com.fish_diseases.biodata_service.dtos.CreateFishDTO;
import com.fish_diseases.biodata_service.dtos.UpdateFishDTO;
import com.fish_diseases.biodata_service.entities.Fish;
import com.fish_diseases.biodata_service.services.FishService;
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
@RequestMapping("/fishes")
@Tag(name = "Peces", description = "Descripción taxonómica de los peces.")
public class FishController {

	@Autowired
	private FishService fishService;

	@Autowired
	private ResponseUtil responseUtil;

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Devuelve todos los peces
	 * 
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping
	public ResponseEntity<?> list() {
		List<Fish> fishes = fishService.findAll();
		if (fishes.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(fishes);
	}

	/**
	 * Devuelve pez por Id
	 * 
	 * @param id del pez
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/{id}")
	public ResponseEntity<?> getFishById(@PathVariable Long id) {
		Fish fish = fishService.findById(id);
		return ResponseEntity.ok(fish);
	}

	/**
	 * Devuelve pez por scientificName
	 * 
	 * @param scientificName del pez
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/sn/{scientificName}")
	public ResponseEntity<?> getFishByScientificName(@PathVariable String scientificName) {
		Fish fish = fishService.findByScientificName(scientificName);
		return ResponseEntity.ok(fish);
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Obtiene datos de pez desde API externa por scientificName
	 * 
	 * @param scientificName del pez
	 * @return ResponseEntity
	 */
	@ApiResponses({
		@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
		@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/fetch/{scientificName}")
	public ResponseEntity<?> fetchFishData(@PathVariable String scientificName) {
		return fishService
				.findOrFetchAndPrepareFish(scientificName)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElseGet(() -> responseUtil.error("error.hostNotFound", HttpStatus.NOT_FOUND));
	}

	/**
	 * Crea nuevo pez en la base de datos
	 * 
	 * @param createFishDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = CREATED_CODE, description = CREATED_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping
	public ResponseEntity<?> createFish(@Valid @RequestBody CreateFishDTO createFishDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		fishService.saveFish(createFishDTO);
		return responseUtil.success("message.fishCreated", HttpStatus.CREATED);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualizar pez por scientificName
	 * 
	 * @param scientificName de pez
	 * @param updateFishDTO
	 * @param result
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PatchMapping("/{scientificName}")
	public ResponseEntity<?> updateFish(@PathVariable String scientificName,
			@RequestBody @Valid UpdateFishDTO updateFishDTO, BindingResult result) {

		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		fishService.findByScientificName(scientificName);

		return fishService.updateFish(scientificName, updateFishDTO)
				.map(updatedFish -> responseUtil.success("message.fishUpdated", HttpStatus.OK))
				.orElseGet(() -> responseUtil.error("error.fishNotUpdated", HttpStatus.NO_CONTENT));
	}

	////////////////////////////////
	//// MÉTODOS DE ELIMINACIÓN ////
	////////////////////////////////

	/**
	 * Eliminar pez por scientificName
	 * 
	 * @param scientificName de pez
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fish.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@DeleteMapping("/{scientificName}")
	public ResponseEntity<?> deleteFish(@PathVariable String scientificName) {
		fishService.deleteFish(scientificName);
		return responseUtil.success("message.fishDeleted", HttpStatus.OK);

	}

}
