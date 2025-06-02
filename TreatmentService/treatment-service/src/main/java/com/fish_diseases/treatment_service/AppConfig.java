package com.fish_diseases.treatment_service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Clase de configuración que carga las propiedades desde el archivo
 * `messages.properties`para ser utilizadas en toda la aplicación.
 */
@Configuration
@PropertySource("classpath:messages.properties")
public class AppConfig {

}
