```ascii
 /´\\	 ____  ____  _  _       __ _   __   _  _  _  _  ____  __     __   ____  ____ 
((--))	(    \(  __)/ )( \ ___ (  ( \ / _\ / )( \/ )( \(  __)(  )   / _\ (    \(  __)
 \\´/	 ) D ( ) _) \ \/ /(___)/    //    \) __ () \/ ( ) _) / (_/\/    \ ) D ( ) _) 
 /´\\	(____/(____) \__/      \_)__)\_/\_/\_)(_/\____/(____)\____/\_/\_/(____/(____)
((--))       :: Desarrollos-NahuelAde ::                       (v1.0.0 RELEASE)
 \\´/
```

# Fish-Diseases - Proyecto de Microservicios

## _Desarrollado por: Nahuel Ade_

- **Organización:** Desarrollos Nahuel Ade

Este proyecto está compuesto por varios microservicios desarrollados con Spring Boot y desplegados con Docker Compose. Cada microservicio cumple una función específica: autenticación de usuarios, gestión de datos biológicos y tratamientos, centralizados a través de un API Gateway.

---

## _Estructura de los Servicios_

- **auth-service**: Servicio de autenticación (JWT + roles).
- **biodata-service**: Gestión de datos biológicos (peces, parásitos).
- **treatment-service**: Gestión de tratamientos y métodos de laboratorio.
- **gateway-service**: API Gateway que centraliza los accesos a los servicios anteriores.


### Estructura del proyecto
```ascii
fish-diseases/
├── ApiGateway/
├── AuthService/
├── BiodataService/
├── TreatmentService/
├── docker-compose.yml
├── .env
├── .gitignore
└── README.md
```
Cada carpeta de servicio contiene su propio Dockerfile, código fuente y configuración Spring Boot.

---

## _Requisitos_

- [Docker](https://www.docker.com/products/docker-desktop/)  
- [Docker Compose](https://docs.docker.com/compose/install/)  
  > Nota: Docker Compose ya viene integrado con Docker Desktop en versiones recientes.
- Puertos disponibles: `8888`, `9999`, `7777`, `9000`, `3307`, `5433`, `5434`

---

## _Ejecución del proyecto_

### 1. Construir y levantar los contenedores

Desde la raíz del proyecto (fish-diseases-app/), ejecuta:

```bash
docker-compose up -d --build
```
Este comando construye automáticamente las imágenes Docker de cada microservicio usando los Dockerfile incluidos en el repositorio.


### 2. Endpoints y pruebas

Cada servicio tiene su propia interfaz Swagger disponible:
- [Auth Service - Swagger UI](http://localhost:8888/swagger-ui.html)
- [Biodata Service - Swagger UI](http://localhost:9999/swagger-ui.html)
- [Treatment Service - Swagger UI](http://localhost:7777/swagger-ui.html)

Ejecución de los endpoints a través del Gateway:
- [API Gateway - Swagger UI](http://localhost:9000/swagger-ui.html)

**Nota**: Swagger solo es accesible si el servicio correspondiente está en ejecución.


### 3. Apagar servicios

```bash
docker-compose down            # Apaga todos los servicios
docker-compose down -v         # Apaga y elimina volúmenes
```

---

## Usuarios de prueba

ROLE_ADMIN:
```json
{
  "username": "admin",
  "password": "Admin123*"
}
```

ROLE_TREATMENT:
```json
{
  "username": "treatment",
  "password": "Treat123*"
}
```

---

## Bases de datos

Se utilizan dos contenedores de bases de datos:

- MySQL para auth-service (puerto 3307)
- PostgreSQL para biodata y treatment (puertos 5433 y 5434)

Estos contenedores se levantan automáticamente con Docker Compose.

Si se quiere iniciar una base manualmente por separado, por ejemplo MySQL, se podría hacer:

```bash
docker run -d --name mysql-auth \
  -e MYSQL_ROOT_USERNAME=root \
  -e MYSQL_ROOT_PASSWORD=F15h@D15ea5e5 \
  -e MYSQL_DATABASE=fish_diseases_auth \
  -p 3307:3306 mysql:latest
```
Reemplazar las variables si es necesario. Ideal para pruebas fuera del entorno Docker Compose.

---

## Notas

  > No se incluyen archivos .tar de imágenes Docker. Las imágenes se construyen directamente desde el código fuente, lo cual es ideal para entornos de desarrollo colaborativo y CI/CD.

---

## Licencia

Este proyecto está disponible para uso libre, educativo o personal. Cualquier redistribución o modificación debe mencionar al autor original: **Nahuel Ade**.

© Desarrollado por [Nahuel Ade](https://github.com/NahuelAde)
