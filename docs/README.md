# Workeando ERP (Workeando 2.0)

Proyecto de demostración en Spring Boot como parte de la plataforma Workeando 2.0.

---

## Índice

* [Requisitos previos](#requisitos-previos)
* [Primeros pasos](#primeros-pasos)
* [Instalación](#instalación)
* [Configuración](#configuración)
* [Ejecución de la aplicación](#ejecución-de-la-aplicación)
* [Datos y credenciales predeterminados](#datos-y-credenciales-predeterminados)
* [Estructura del proyecto](#estructura-del-proyecto)
* [Tecnologías utilizadas](#tecnologías-utilizadas)
* [Referencias externas](#referencias-externas)
* [Contribuir](#contribuir)
* [Licencia](#licencia)

---

## Requisitos previos

* **Java 17** o superior
* **Maven 3.8** o superior (incluido mediante el wrapper `mvnw`)
* **PostgreSQL** (dos bases de datos)
* **Docker** (opcional, para construcciones en contenedores)
* IDE (IntelliJ IDEA, VSCode, Eclipse)

---

## Primeros pasos

1. Clona el repositorio:

   ```bash
   git clone <https://github.com/josssruizz/workeando2.0.git>
   cd Workeando_2.0/Workeando_2.0
   ```
2. Asegúrate de que PostgreSQL esté en funcionamiento y crea dos bases de datos:

   ```sql
   CREATE DATABASE bd_seguridad;
   CREATE DATABASE bd_dominio;
   ```

---

## Instalación

### Usando el wrapper de Maven

Compila y empaqueta:

```bash
./mvnw clean install
```

Ejecuta las pruebas:

```bash
./mvnw test
```

### Imagen Docker (Opcional)

Construye una imagen Docker usando Cloud Native Buildpacks de Spring Boot:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=workeando/erp:latest
```

Ejecuta el contenedor:

```bash
docker run -p 8082:8082 --env-file .env workeando/erp:latest
```

---

## Configuración

Edita las propiedades en `src/main/resources/application.properties`:

```properties
spring.application.name=workeando
server.port=8082

# Base de datos de seguridad
spring.datasource.seguridad.jdbc-url=jdbc:postgresql://localhost:5432/bd_seguridad
spring.datasource.seguridad.username=postgres
spring.datasource.seguridad.password=database

# Base de datos de dominio
spring.datasource.dominio.jdbc-url=jdbc:postgresql://localhost:5432/bd_dominio
spring.datasource.dominio.username=postgres
spring.datasource.dominio.password=database

# Configuración JWT
jwt.secret=<tu-secret-key>
jwt.expiration=86400000
```

* Reemplaza `database` y `<tu-secret-key>` con valores seguros en un archivo `.env` o en un gestor de secretos.

---

## Ejecución de la aplicación

Inicia la aplicación con Maven:

```bash
./mvnw spring-boot:run
```

O ejecuta el JAR empaquetado:

```bash
java -jar target/erp-0.0.1-SNAPSHOT.jar
```

Accede a la interfaz web en [http://localhost:8082](http://localhost:8082).

---

## Datos y credenciales predeterminados

En la primera ejecución, el componente `DataLoader` inserta roles, permisos y un usuario administrador:

* **Email:** `admin@workeando.com`
* **Password:** `admin123`

Roles iniciales: `ROLE_ADMIN`, `ROLE_USER`, etc.

---

## Estructura del proyecto

```
├── src/main/java/Workeando20/erp
│   ├── config        # Configuraciones de Spring y seguridad
│   ├── dominio       # Entidades de dominio, repositorios, servicios
│   ├── seguridad     # Modelos de seguridad, servicios, JWT
│   └── WorkeandoApplication.java  # Punto de entrada principal
├── src/main/resources
│   └── application.properties
├── pom.xml           # Descripción del proyecto Maven
└── HELP.md           # Referencia rápida y configuraciones adicionales
```

---

## Tecnologías utilizadas

* **Spring Boot 3.5.3**
* **Spring Security (JWT)**
* **Spring Data JPA**
* **Thymeleaf**
* **PostgreSQL**
* **Maven Wrapper**
* **Buildpacks/Docker**

---

## Referencias externas

* [Guías de Apache Maven](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/3.5.3/maven-plugin)
* [Spring Web & Thymeleaf](https://docs.spring.io/spring-boot/3.5.3/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.3/reference/web/spring-security.html)

---

## Contribuir

1. Haz un fork del repositorio
2. Crea una rama de funciones (`git checkout -b feature/XYZ`)
3. Realiza commits (`git commit -m "Añadir función XYZ"`)
4. Sube la rama (`git push origin feature/XYZ`)
5. Abre un Pull Request

---

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
