package com.fish_diseases.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para la aplicación API Gateway del sistema de gestión de enfermedades en peces.
 * 
 * <p>Esta clase contiene el método principal que inicia la aplicación Spring Boot. Inicia el contexto de la
 * aplicación y configura todos los componentes necesarios para ejecutar el Gateway.</p>
 * 
 * <p>El API Gateway actúa como punto de entrada único para todas las solicitudes externas al sistema,
 * facilitando la comunicación entre clientes y los diferentes microservicios que componen la plataforma.</p>
 * 
 * <p>La configuración incluye:</p>
 * <ul>
 *     <li><b>Ruteo de peticiones:</b> Redirecciona las solicitudes a los servicios correspondientes según la configuración.</li>
 *     <li><b>Seguridad:</b> Puede aplicar filtros de seguridad como autenticación basada en JWT antes de redirigir el tráfico.</li>
 *     <li><b>Balanceo de carga:</b> Distribuye las solicitudes entre múltiples instancias de servicios.</li>
 *     <li><b>Monitoreo y trazabilidad:</b> Facilita el seguimiento de peticiones y la detección de errores.</li>
 *     <li><b>Escalabilidad:</b> Permite escalar la arquitectura al desacoplar responsabilidades entre servicios.</li>
 * </ul>
 * 
 * @author Desarrollos-NahuelAde
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
