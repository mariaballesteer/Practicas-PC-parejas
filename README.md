# todo-api

API REST de gestión de tareas con **Spring Boot 3.5**, **Java 21** y **Maven**.
Persistencia en memoria (los datos se pierden al parar la aplicación).

## Requisitos

- Git
- JDK 21
- Maven 3.9 o superior

## Puesta en marcha desde cero

    git clone https://github.com/mariaballesteer/Practicas-PC-parejas.git
    cd Practicas-PC-parejas
    git config core.hooksPath .githooks
    mvn clean package
    java -jar target/todo-api.jar

El `git config` activa el hook de pre-commit, que necesita Maven para formatear el código antes de cada commit. La API queda escuchando en http://localhost:8080.

## Arquitectura

```
controller/  -> TareaController        (HTTP, validación de entrada)
service/     -> TareaService           (reglas de negocio)
repository/  -> TareaRepository        (interfaz)
                TareaRepositoryEnMemoria (implementación con ConcurrentSkipListMap)
model/       -> Tarea, EstadoTarea, Prioridad
dto/         -> TareaRequest, TareaResponse, CambioEstadoRequest, ErrorRespuesta
exception/   -> excepciones propias + GlobalExceptionHandler (@RestControllerAdvice)
```

## Compilar, probar y ejecutar

```bash
mvn test                      # solo tests unitarios
mvn package                   # tests + fat JAR en target/todo-api.jar
java -jar target/todo-api.jar # arranca en http://localhost:8080
```

## Endpoints

| Método | Ruta                       | Descripción                              | Respuesta |
|--------|----------------------------|------------------------------------------|-----------|
| POST   | `/api/tareas`              | Crear tarea                              | 201       |
| GET    | `/api/tareas`              | Listar (`?estado=` `?prioridad=`)        | 200       |
| GET    | `/api/tareas/{id}`         | Obtener una tarea                        | 200 / 404 |
| PUT    | `/api/tareas/{id}`         | Actualizar datos                         | 200 / 404 |
| PATCH  | `/api/tareas/{id}/estado`  | Cambiar estado                           | 200 / 404 |
| DELETE | `/api/tareas/{id}`         | Eliminar                                 | 204 / 404 |

Valores: `prioridad` = `BAJA | MEDIA | ALTA`; `estado` = `PENDIENTE | EN_PROGRESO | COMPLETADA`.

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
