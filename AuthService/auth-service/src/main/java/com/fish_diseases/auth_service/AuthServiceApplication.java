package com.fish_diseases.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para la aplicación de servicio de autenticación en el sistema de gestión de enfermedades en peces.
 * 
 * <p>Esta clase contiene el método principal que arranca la aplicación Spring Boot. Inicia el contexto de la aplicación
 * y configura todos los componentes necesarios para ejecutar el servicio de autenticación.</p>
 * 
 * <p>El servicio proporciona funcionalidades para el registro, inicio de sesión, gestión y autenticación de usuarios.</p>
 * 
 * <p>La aplicación está diseñada para integrarse con un sistema de gestión de enfermedades en peces, proporcionando
 * una interfaz segura para que los usuarios puedan autenticar y gestionar su acceso al sistema.</p>
 * 
 * <p>La configuración incluye:</p>
 * <ul>
 *     <li><b>Autenticación de usuarios:</b> El servicio maneja el registro, inicio de sesión y la autenticación de usuarios.</li>
 *     <li><b>Seguridad:</b> El sistema está protegido por un mecanismo de autenticación basado en JWT.</li>
 *     <li><b>Integración con otros servicios:</b> Este servicio se integra con el resto del sistema de gestión de enfermedades en peces, permitiendo un control de acceso adecuado.</li>
 *     <li><b>Escalabilidad:</b> La arquitectura está diseñada para ser escalable y permitir su uso en ambientes de producción de alta carga.</li>
 * </ul>
 * 
 * @author Desarrollos-NahuelAde
 * @version 1.0.0
 */
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
