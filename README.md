# todo-api

API REST de gestión de tareas con **Spring Boot 3.5**, **Java 21** y **Maven**.
Persistencia en **PostgreSQL** mediante Spring Data JPA (Hibernate).

## Requisitos

- Git
- JDK 21
- Maven 3.9 o superior
- Docker y Docker Compose (solo si vas a usar alguna de las dos opciones de abajo)

## Puesta en marcha desde cero

    git clone https://github.com/mariaballesteer/Practicas-PC-parejas.git
    cd Practicas-PC-parejas
    git config core.hooksPath .githooks
    mvn clean package
    java -jar target/todo-api.jar

El `git config` activa el hook de pre-commit, que necesita Maven para formatear el código antes de cada commit. La API queda escuchando en http://localhost:8080.

Esta opción necesita un PostgreSQL accesible en `localhost:5432` (ver variables en [Configuración de la base de datos](#configuración-de-la-base-de-datos)). Si no quieres instalar PostgreSQL a mano, usa la opción con [Docker](#levantar-todo-con-docker) de abajo, que también arranca la base de datos.

## Arquitectura

```
controller/  -> TareaController        (HTTP, validación de entrada)
service/     -> TareaService           (reglas de negocio)
repository/  -> TareaRepository        (interfaz)
                TareaRepositoryJpa      (implementación con Spring Data JPA / PostgreSQL)
                TareaJpaRepository      (interfaz JpaRepository)
                TareaRepositoryEnMemoria (doble en memoria, solo usado en tests)
model/       -> Tarea (entidad JPA), EstadoTarea, Prioridad
dto/         -> TareaRequest, TareaResponse, CambioEstadoRequest, ErrorRespuesta
exception/   -> excepciones propias + GlobalExceptionHandler (@RestControllerAdvice)
```

## Compilar, probar y ejecutar

```bash
mvn test                      # solo tests unitarios
mvn package                   # tests + fat JAR en target/todo-api.jar
java -jar target/todo-api.jar # arranca en http://localhost:8080
```

## Configuración de la base de datos

La aplicación lee estas variables de entorno (con valores por defecto para desarrollo local en [`application.properties`](src/main/resources/application.properties)):

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/tododb` | URL JDBC de PostgreSQL |
| `DB_USER` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | `postgres` | Contraseña de la base de datos |

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate crea/actualiza la tabla `tareas` automáticamente al arrancar; no hace falta ejecutar ningún script SQL a mano.

## Levantar todo con Docker

El repositorio incluye un [`Dockerfile`](Dockerfile) (build multi-stage) y un [`docker-compose.yml`](docker-compose.yml) que levanta la API y una base de datos PostgreSQL juntas.

1. Copia el fichero de variables de entorno de ejemplo y ajústalo si quieres:

       cp .env.example .env

   `docker-compose.yml` lee `POSTGRES_DB`, `POSTGRES_USER` y `POSTGRES_PASSWORD` de ese `.env` (Docker Compose lo carga automáticamente, no hace falta exportarlo a mano). El fichero `.env` está en `.gitignore`: cada persona tiene el suyo y no se sube al repositorio.

2. Levanta los servicios:

       docker compose up --build

   Esto construye la imagen de la API, arranca PostgreSQL, espera a que esté sana (`healthcheck`) y entonces arranca la API conectada a esa base de datos (`db:5432`, dentro de la red de Docker). La API queda en http://localhost:8080 y Postgres accesible en `localhost:5432` solo desde tu máquina.

3. Para parar los servicios:

       docker compose down          # para los contenedores, conserva los datos
       docker compose down -v       # además borra el volumen de la base de datos

## Levantar el proyecto con Dev Container

Si usas VS Code con la extensión **Dev Containers**, el repositorio ya trae la configuración en [`.devcontainer/devcontainer.json`](.devcontainer/devcontainer.json):

1. Abre la carpeta del proyecto en VS Code.
2. Cuando aparezca el aviso "Reopen in Container", acéptalo (o usa la paleta de comandos: `Dev Containers: Reopen in Container`).

El contenedor incluye Java 21, Maven y Docker-in-Docker (para poder usar `docker compose` desde dentro), y expone los puertos `8080` (API) y `5432` (PostgreSQL).

Una vez dentro del Dev Container, para arrancar la base de datos y la API puedes:

- Usar Docker Compose igual que en la sección anterior (`docker compose up --build`), o
- Levantar solo la base de datos con Compose (`docker compose up -d db`) y ejecutar la API directamente con Maven (`mvn spring-boot:run`).

## Endpoints

| Método | Ruta                       | Descripción                              | Respuesta |
|--------|----------------------------|------------------------------------------|-----------|
| POST   | `/api/tareas`              | Crear tarea                              | 201       |
| GET    | `/api/tareas`              | Listar con filtros opcionales: `estado`, `prioridad`, `fechaDesde`, `fechaHasta`, `orden` | 200 |
| GET    | `/api/tareas/{id}`         | Obtener una tarea                        | 200 / 404 |
| PUT    | `/api/tareas/{id}`         | Actualizar datos                         | 200 / 404 |
| PATCH  | `/api/tareas/{id}/estado`  | Cambiar estado                           | 200 / 404 |
| DELETE | `/api/tareas/{id}`         | Eliminar                                 | 204 / 404 |

`fechaDesde` y `fechaHasta` usan el formato `AAAA-MM-DD` y delimitan un rango inclusivo. Valores: `prioridad` = `BAJA | MEDIA | ALTA`; `estado` = `PENDIENTE | EN_PROGRESO | COMPLETADA`; `orden` = `FECHA_LIMITE | PRIORIDAD`.

## Códigos de error

| Código | Cuándo |
|--------|--------|
| 400 | Validación fallida (título vacío, JSON mal formado, enum inválido...) |
| 404 | La tarea (o la ruta) no existe |
| 422 | Se incumple una regla de negocio |
| 500 | Error inesperado (mensaje genérico, sin traza) |

## Reglas de negocio

1. La fecha límite no puede ser anterior a hoy.
2. Las tareas de prioridad `ALTA` deben tener fecha límite.
3. No puede haber dos tareas activas (no completadas) con el mismo título (sin distinguir mayúsculas).
4. Transiciones de estado: `PENDIENTE -> EN_PROGRESO -> COMPLETADA` (y `EN_PROGRESO -> PENDIENTE`).
5. Máximo 3 tareas `EN_PROGRESO` a la vez.
6. Una tarea completada no se puede modificar.
7. Una tarea en progreso no se puede eliminar.

## Ejemplos con curl

```bash
# Crear
curl -i -X POST localhost:8080/api/tareas -H "Content-Type: application/json" \
  -d '{"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","fechaLimite":"2030-12-31"}'

# Listar
curl localhost:8080/api/tareas
curl "localhost:8080/api/tareas?estado=PENDIENTE&prioridad=ALTA"

# Obtener (200) y obtener inexistente (404)
curl localhost:8080/api/tareas/1
curl -i localhost:8080/api/tareas/999

# Actualizar
curl -X PUT localhost:8080/api/tareas/1 -H "Content-Type: application/json" \
  -d '{"titulo":"Estudiar Spring Boot","prioridad":"MEDIA","fechaLimite":"2030-12-31"}'

# Cambiar estado
curl -X PATCH localhost:8080/api/tareas/1/estado -H "Content-Type: application/json" \
  -d '{"estado":"EN_PROGRESO"}'

# Regla de negocio incumplida (422): fecha límite pasada
curl -i -X POST localhost:8080/api/tareas -H "Content-Type: application/json" \
  -d '{"titulo":"Tarea antigua","prioridad":"BAJA","fechaLimite":"2020-01-01"}'

# Eliminar
curl -i -X DELETE localhost:8080/api/tareas/1
```

## Formato del código

Uso Spotless (con el estilo de Google) para que todo el código tenga el mismo formato.

- `mvn spotless:check` comprueba si el código está bien formateado.
- `mvn spotless:apply` lo formatea.

Hay un hook de pre-commit en `.githooks/` que ejecuta `spotless:apply` antes de cada commit.
Después de clonar el repositorio hay que activarlo una vez con:

    git config core.hooksPath .githooks
