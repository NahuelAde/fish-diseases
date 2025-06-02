package com.fish_diseases.treatment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para la aplicación del servicio de tratamientos en el sistema de gestión de enfermedades en peces.
 * 
 * <p>Esta clase contiene el método principal que arranca la aplicación Spring Boot. Inicia el contexto de la aplicación
 * y configura todos los componentes necesarios para ejecutar el servicio de tratamientos.</p>
 * 
 * <p>El servicio permite gestionar la información relacionada con métodos de laboratorio y tratamientos aplicables a enfermedades en peces.</p>
 * 
 * <p>La aplicación está diseñada para integrarse con el sistema general de gestión de enfermedades en peces, proporcionando
 * herramientas para registrar y consultar tratamientos efectivos.</p>
 * 
 * <p>La configuración incluye:</p>
 * <ul>
 *     <li><b>Gestión de tratamientos:</b> Registro y mantenimiento de tratamientos médicos para peces afectados.</li>
 *     <li><b>Métodos de laboratorio:</b> Registro de técnicas empleadas para la detección y estudio de enfermedades.</li>
 *     <li><b>Fiabilidad y trazabilidad:</b> Sistema robusto que permite mantener un historial de cambios y actualizaciones.</li>
 * </ul>
 * 
 * @author Desarrollos-NahuelAde
 * @version 1.0.0
 */
@SpringBootApplication
public class TreatmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TreatmentServiceApplication.class, args);
	}

}
