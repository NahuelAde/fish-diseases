package com.fish_diseases.biodata_service.init;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fish_diseases.biodata_service.entities.Fish;
import com.fish_diseases.biodata_service.entities.Parasite;
import com.fish_diseases.biodata_service.entities.Taxonomy;
import com.fish_diseases.biodata_service.repositories.FishRepository;
import com.fish_diseases.biodata_service.repositories.ParasiteRepository;

/**
 * Clase que implementa {@link CommandLineRunner} para inicializar datos de prueba en la base de datos.
 * Esta clase se ejecuta al inicio de la aplicación y crea registros de {@link Fish} y {@link Parasite}
 * si no existen en el repositorio.
 */
@Component
public class BiodataTestDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BiodataTestDataInitializer.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

    private final FishRepository fishRepository;
    private final ParasiteRepository parasiteRepository;

    /**
     * Constructor de la clase {@link BiodataTestDataInitializer}.
     * 
     * @param fishRepository Repositorio de {@link Fish}.
     * @param parasiteRepository Repositorio de {@link Parasite}.
     */
    public BiodataTestDataInitializer(FishRepository fishRepository, ParasiteRepository parasiteRepository) {
        this.fishRepository = fishRepository;
        this.parasiteRepository = parasiteRepository;
    }

    /**
     * Método que inicializa los datos de prueba si no existen registros con los nombres científicos 
     * específicos para {@link Fish} y {@link Parasite}.
     * 
     * <p>El método crea un {@link Fish} con un {@link Taxonomy} y lo guarda en el repositorio, 
     * luego crea un {@link Parasite} relacionado y lo guarda también. Finalmente, se vincula el {@link Parasite} al {@link Fish}.</p>
     */
    @Override
    public void run(String... args) {
        if (fishRepository.findByScientificName("Testus fishus").isEmpty() &&
            parasiteRepository.findByScientificName("Parasiticus testus").isEmpty()) {

    		if (isTraceEnabled) {
    			log.trace("######## Start BiodataTestDataInitializer ########");
    		}

            Fish fish = Fish.builder()
                .scientificName("Testus fishus")
                .commonName("Pez Test")
                .taxonomy(Taxonomy.builder()
                    .aphiaID(123456)
                    .kingdom("Animalia")
                    .phylum("Chordata")
                    .className("Actinopterygii")
                    .orderName("Perciformes")
                    .family("Cichlidae")
                    .genus("Testus")
                    .species("fishus")
                    .url("https://example.com/fish/testus")
                    .build())
                .faoDistribution("Atlántico, Pacífico")
                .build();

            fish = fishRepository.save(fish);

            Parasite parasite = Parasite.builder()
                .scientificName("Parasiticus testus")
                .commonName("Parásito Test")
                .taxonomy(Taxonomy.builder()
                    .aphiaID(654321)
                    .kingdom("Animalia")
                    .phylum("Platyhelminthes")
                    .className("Trematoda")
                    .orderName("Plagiorchiida")
                    .family("Testidae")
                    .genus("Parasiticus")
                    .species("testus")
                    .url("https://example.com/parasite/testus")
                    .build())
                .diagnosticFeatures("Forma ovalada, cuerpo blando, ventosa oral.")
                .size("0.2 - 0.5 mm")
                .geographicalDistribution("Mundial")
                .biologicalCycle("Incluye hospedadores intermediarios y definitivos.")
                .hostSpecificity("Altamente específico para peces tropicales.")
                .locationOnHost("Branquias y cavidad bucal")
                .damagesOnHost("Lesiones epiteliales, obstrucción respiratoria")
                .detection("Microscopía directa de branquias")
                .fishes(Set.of(fish))
                .build();

            parasite = parasiteRepository.save(parasite);

            fish.setParasites(Set.of(parasite));
            fishRepository.save(fish);

    		if (isTraceEnabled) {
    			log.trace("######## End BiodataTestDataInitializer ########");
    		}

        }
    }
}
