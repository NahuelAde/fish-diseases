package com.fish_diseases.biodata_service.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fish_diseases.biodata_service.dtos.ApiTaxonomyDTO;
import com.fish_diseases.biodata_service.mapper.TaxonomyMapper;
import com.fish_diseases.biodata_service.utils.ResponseUtil;

@Component
public class BioDataClient {

    private final WebClient webClient;
    private final TaxonomyMapper taxonomyMapper;

    public BioDataClient(WebClient webClient, ResponseUtil responseUtil, TaxonomyMapper taxonomyMapper) {
        this.webClient = webClient;
        this.taxonomyMapper = taxonomyMapper;
    }

    /**
     * Obtiene los datos taxonómicos de WoRMS a partir del nombre científico.
     *
     * @param scientificName Nombre científico del organismo a buscar.
     * @return Un Optional con Map<String, Object> que representa los datos de taxonomía, o vacío si no se encuentran datos.
     */
    public Optional<Map<String, Object>> fetchBioData(String scientificName) {
        Optional<ApiTaxonomyDTO> taxonomyOpt = fetchFromWoRMS(scientificName);
        return taxonomyOpt.map(taxonomyMapper::convertApiTaxonomyDtoToMap);
    }

    /**
     * Llama a la API de WoRMS y obtiene los datos taxonómicos como un ApiTaxonomyDTO.
     *
     * @param scientificName Nombre científico del organismo.
     * @return Optional<ApiTaxonomyDTO> si hay resultados, vacío en caso contrario.
     */
    public Optional<ApiTaxonomyDTO> fetchFromWoRMS(String scientificName) {

        String uri = "/AphiaRecordsByName/" + scientificName + "?marine_only=false";

        Map<String, Object>[] response = webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>[]>() {})
            .block();

        if (response != null && response.length > 0) {
            Map<String, Object> data = response[0];
            ApiTaxonomyDTO taxonomy = TaxonomyMapper.convertMapToApiTaxonomyDto(data);
            return Optional.of(taxonomy);
        }

        return Optional.empty();
    }

    /**
     * Obtiene los IDs (measurementValue) a partir de un AphiaID usando el endpoint AphiaAttributesByAphiaID,
     * filtrando por el measurementType especificado.
     *
     * @param aphiaID              El AphiaID a consultar.
     * @param measurementTypeFilter El measurementType que se desea filtrar (por ejemplo, "Host/prey").
     * @return Una lista de IDs (como String) que cumplen con el filtro.
     */
    public List<String> fetchBiodataIdsByAphiaID(String aphiaID, String measurementTypeFilter) {
        String uri = "/AphiaAttributesByAphiaID/" + aphiaID;
        List<Map<String, Object>> response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>(){})
                .block();
        List<String> ids = new ArrayList<>();
        if (response != null) {
            for (Map<String, Object> attribute : response) {
                collectMeasurementIds(attribute, ids, measurementTypeFilter);
            }
        }
        return ids;
    }

    /**
     * Método auxiliar recursivo para extraer measurementValues de la estructura anidada,
     * filtrando por el measurementType especificado.
     *
     * @param attribute             El mapa de atributos.
     * @param ids                   La lista donde se acumulan los IDs encontrados.
     * @param measurementTypeFilter El tipo de medición a buscar.
     */
    @SuppressWarnings("unchecked")
    private void collectMeasurementIds(Map<String, Object> attribute, List<String> ids, String measurementTypeFilter) {
        String type = (String) attribute.get("measurementType");
        if (type != null && type.equalsIgnoreCase(measurementTypeFilter)) {
            Object value = attribute.get("measurementValue");
            if (value != null) {
                ids.add(value.toString());
            }
        }
        // Si existen children, recorrerlos recursivamente
        Object childrenObj = attribute.get("children");
        if (childrenObj instanceof List<?>) {
            List<?> children = (List<?>) childrenObj;
            for (Object child : children) {
                if (child instanceof Map) {
                    collectMeasurementIds((Map<String, Object>) child, ids, measurementTypeFilter);
                }
            }
        }
    }


    /**
     * Obtiene el scientificName a partir de un AphiaID usando la API de WoRMS.
     *
     * @param aphiaID El identificador de WoRMS.
     * @return ResponseEntity con el nombre científico o un error si no se encuentra.
     */
    public ResponseEntity<String> getScientificNameByAphiaID(String aphiaID) {
        String uri = "/AphiaRecordByAphiaID/" + aphiaID;
        try {
            Map<String, Object> response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            if (response != null) {
                String scientificName = (String) response.getOrDefault("scientificname", response.get("scientificName"));
                if (scientificName != null) {
                    return ResponseEntity.ok(scientificName);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error.parasiteNotFound");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error.fetchingData");
        }
    }
}
