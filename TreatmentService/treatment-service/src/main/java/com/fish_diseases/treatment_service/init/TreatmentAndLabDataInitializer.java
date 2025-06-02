package com.fish_diseases.treatment_service.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fish_diseases.treatment_service.entities.LaboratoryMethod;
import com.fish_diseases.treatment_service.entities.Treatment;
import com.fish_diseases.treatment_service.repositories.LaboratoryMethodRepository;
import com.fish_diseases.treatment_service.repositories.TreatmentRepository;

/**
 * Componente de inicialización de datos que se ejecuta al iniciar la aplicación.
 * Verifica si existen ciertos tratamientos y métodos de laboratorio predefinidos
 * y, de no existir, los crea con valores por defecto.
 */
@Component
public class TreatmentAndLabDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TreatmentAndLabDataInitializer.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

    private final TreatmentRepository treatmentRepository;
    private final LaboratoryMethodRepository labMethodRepository;

    /**
     * Constructor que inyecta los repositorios requeridos.
     *
     * @param treatmentRepository repositorio de tratamientos
     * @param labMethodRepository repositorio de métodos de laboratorio
     */
    public TreatmentAndLabDataInitializer(TreatmentRepository treatmentRepository, LaboratoryMethodRepository labMethodRepository) {
        this.treatmentRepository = treatmentRepository;
        this.labMethodRepository = labMethodRepository;
    }

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     * Inserta datos de prueba si no existen en la base de datos.
     *
     * @param args argumentos de línea de comandos
     */
    @Override
    public void run(String... args) {

		if (isTraceEnabled) {
			log.trace("######## Start TreatmentAndLabDataInitializer ########");
		}

        if (treatmentRepository.findByName("Tratamiento Test").isEmpty()) {
            Treatment testTreatment = Treatment.builder()
                .name("Tratamiento Test")
                .description("Este tratamiento experimental es utilizado en peces tropicales para el control de parásitos externos.")
                .actionSpectrum("Actúa contra monogeneos y crustáceos ectoparásitos.")
                .dose("Aplicar 20 mg/L durante 30 minutos, repetir cada 3 días.")
                .build();

            treatmentRepository.save(testTreatment);
         }

        if (labMethodRepository.findByName("Microscopía directa").isEmpty()) {
            LaboratoryMethod testMethod = LaboratoryMethod.builder()
                .name("Microscopía directa")
                .technique("Consiste en la observación directa de muestras frescas o teñidas de tejido bajo un microscopio para detectar la presencia de estructuras parasitarias.")
                .build();

            labMethodRepository.save(testMethod);

    		if (isTraceEnabled) {
    			log.trace("######## End TreatmentAndLabDataInitializer ########");
    		}

        }
    }
}
