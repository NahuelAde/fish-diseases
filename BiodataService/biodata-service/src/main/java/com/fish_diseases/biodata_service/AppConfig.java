package com.fish_diseases.biodata_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Clase de configuración que carga las propiedades desde el archivo
 * `messages.properties`para ser utilizadas en toda la aplicación.
 */
@Configuration
@PropertySource("classpath:messages.properties")
public class AppConfig {

    /**
     * Crea y configura un bean {@link WebClient} con la URL base de WoRMS.
     *
     * @param builder Constructor de {@link WebClient}.
     * @return una instancia de {@link WebClient} lista para hacer peticiones HTTP.
     */
    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://www.marinespecies.org/rest")
                      .build();
    }
}
