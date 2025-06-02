package com.fish_diseases.biodata_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para la aplicación del servicio de datos biológicos en el sistema de gestión de enfermedades en peces.
 * 
 * <p>Esta clase contiene el método principal que arranca la aplicación Spring Boot. Inicia el contexto de la aplicación
 * y configura todos los componentes necesarios para ejecutar el servicio de biodatos.</p>
 * 
 * <p>El servicio proporciona funcionalidades para la gestión de información biológica relacionada con peces y parásitos.</p>
 * 
 * <p>La aplicación está diseñada para integrarse con un sistema de gestión de enfermedades en peces, ofreciendo
 * una base de datos estructurada sobre especies y sus relaciones.</p>
 * 
 * <p>La configuración incluye:</p>
 * <ul>
 *     <li><b>Gestión de peces y parásitos:</b> Permite registrar, actualizar y consultar datos taxonómicos y de distribución.</li>
 *     <li><b>Persistencia de datos:</b> Utiliza una base de datos para almacenar la información biológica relevante.</li>
 *     <li><b>Integración con servicios externos:</b> Puede conectarse a APIs como WoRMS para enriquecer los datos taxonómicos.</li>
 *     <li><b>Interoperabilidad:</b> Este servicio se comunica con otros módulos del sistema para facilitar análisis clínicos y diagnósticos.</li>
 * </ul>
 * 
 * @author Desarrollos-NahuelAde
 * @version 1.0.0
 */
@SpringBootApplication
public class BiodataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiodataServiceApplication.class, args);
	}

}
