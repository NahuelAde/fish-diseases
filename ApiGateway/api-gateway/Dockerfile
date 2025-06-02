FROM openjdk:17-jdk-slim

LABEL author=developments-dna.com

# Compilar ruta específica (.jar) a Docker (mvn clean package)
ARG JAR_FILE=target/api_gateway-0.0.1.jar
COPY ${JAR_FILE} api_gateway.jar

# Instalar curl y limpiar caché
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Exponer el puerto del servicio
EXPOSE 9000

# Expresar comandos a ejecutar al generar contenedor a partir de esta imagen
ENTRYPOINT ["java", "-jar", "/api_gateway.jar"]