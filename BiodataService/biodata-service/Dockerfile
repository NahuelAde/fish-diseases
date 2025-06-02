FROM openjdk:17-jdk-slim

LABEL author=developments-dna.com

# Compilar ruta específica (.jar) a Docker (mvn clean package)
ARG JAR_FILE=target/biodata_service-0.0.1.jar
COPY ${JAR_FILE}  biodata_service.jar

# Instalar curl y limpiar caché
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Exponer el puerto del servicio
EXPOSE 9999

# Expresar comandos a ejecutar al generar contenedor a partir de esta imagen
ENTRYPOINT ["java", "-jar", "/biodata_service.jar"]