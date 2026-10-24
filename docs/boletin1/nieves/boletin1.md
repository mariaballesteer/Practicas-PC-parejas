---
title: "Entrega — Boletín 1: Git y proyecto Maven"
author: "Nieves"
---

# Entrega — Boletín 1: Git y proyecto Maven

Repositorio local: `~/todo-api` (proyecto Maven + Spring Boot 3.3.2, Java 21).

> **Reproducibilidad verificada**: se clonó el repo en una carpeta limpia y se ejecutó `mvn clean verify` sin ningún ajuste previo → BUILD SUCCESS, 10/10 tests, jar arranca y responde. Único paso manual necesario tras clonar: `git config core.hooksPath .githooks` (es config local de Git, no viaja con el repo — está documentado en el `.git/hooks/` del boletín). Sin ese paso el build, los tests y el jar funcionan igual; solo se pierde el auto-formateo en los commits que haga quien clona.

## Parte A — Entorno

```
$ git --version
git version 2.43.0

$ java -version
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-24.04.4-Ubuntu)

$ mvn -version
Apache Maven 3.8.7
Java version: 21.0.12.1, vendor: Ubuntu
```

Identidad y política de pull configuradas:

```bash
git config --global user.name "Nieves"
git config --global user.email "ncopito@gmail.com"
git config --global pull.ff only
git config --global init.defaultBranch main
```

## Parte B — Aplicación generada

**Dominio elegido:** API de gestión de tareas (To-Do), entidad `Task`.

- **Entidad con varios campos y validaciones**: `Task` (id, title, description, status, priority, dueDate). Validaciones Bean Validation en los DTOs de entrada (`@NotBlank`, `@NotNull`, `@FutureOrPresent`).
- **Persistencia**: Spring Data JPA + Hibernate sobre H2 en memoria (`spring.datasource.url=jdbc:h2:mem:tododb`).
- **CRUD completo**: crear, listar (con filtro por estado y prioridad), obtener por id, actualizar, completar, borrar.
- **Capas separadas**: `web` (controller + DTOs + manejo de errores), `service` (lógica de negocio), `repository` (Spring Data JPA), `domain` (entidad y enums).
- **Manejo de errores centralizado**: `GlobalExceptionHandler` con `@RestControllerAdvice` — recurso inexistente → **404**, estado inválido → **409**, validación fallida → **400**. Nunca una traza de excepción cruda.
- **Reglas de negocio reales cubiertas por tests** (10 tests unitarios, `TaskServiceTest`, JUnit 5 + Mockito, sin `@SpringBootTest` — no se levanta contexto ni base de datos):
  1. Crear tarea con fecha límite pasada → `IllegalArgumentException`.
  2. Crear tarea sin prioridad → se asigna `MEDIA` por defecto.
  3. Crear tarea válida → queda en estado `PENDIENTE`.
  4. Completar tarea pendiente → pasa a `COMPLETADA`.
  5. Completar tarea ya completada → `InvalidTaskStateException`.
  6. Buscar tarea inexistente → `TaskNotFoundException`.
  7. Actualizar tarea inexistente → `TaskNotFoundException`.
  8. Eliminar tarea inexistente → `TaskNotFoundException`.
  9. Listar filtrando por estado → devuelve solo las coincidentes.
  10. Listar filtrando por prioridad → devuelve solo las coincidentes.
- **Fat JAR ejecutable**: generado con `spring-boot-maven-plugin` vía `mvn package`, arrancable con `java -jar`.

## Parte C — Construcción y prueba en local

```
$ mvn clean test
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0

$ mvn clean package
[...]
$ ls -la target/*.jar
-rw-rw-r-- 1 nieves nieves 49179253 sep 28 15:35 target/todo-api-0.1.0.jar

$ java -jar target/todo-api-0.1.0.jar
...
Started TodoApiApplication in 2.831 seconds
```

Pruebas con `curl` (aplicación real levantada en `localhost:8080`):

```
--- crear tarea válida ---
POST /api/tasks {"title":"Estudiar Maven","priority":"ALTA","dueDate":"2026-10-05"}
→ 201 {"id":1,"title":"Estudiar Maven",...,"status":"PENDIENTE","priority":"ALTA","dueDate":"2026-10-05"}

--- crear tarea con fecha pasada ---
POST /api/tasks {"title":"Tarea invalida","priority":"BAJA","dueDate":"2020-01-01"}
→ HTTP 400

--- listar tareas ---
GET /api/tasks → [{"id":1,...,"status":"PENDIENTE",...}]

--- completar tarea 1 ---
POST /api/tasks/1/complete → {"id":1,...,"status":"COMPLETADA",...}

--- completar tarea 1 otra vez ---
POST /api/tasks/1/complete → HTTP 409

--- tarea inexistente ---
GET /api/tasks/99 → HTTP 404
{"timestamp":"...","status":404,"error":"Not Found","message":"No existe una tarea con id 99","details":[]}
```

Todos los códigos HTTP y el manejo de errores se comportan como exige el enunciado.

## Parte D — Control de versiones con Git

Archivos base creados: `.gitignore` (`target/`, `*.class`, `.idea/`, `.env`), `.gitattributes` (`* text=auto eol=lf`, `*.jar binary`) y `.editorconfig` (indent 4 para código, 2 para yml/json).

Rama de feature: `feat/filtro-por-prioridad` — añade filtro `?priority=` en `GET /api/tasks` (cambio pequeño pero no trivial: nuevo parámetro en controller + service + test nuevo). Fusionada en `main` con `--no-ff`.

Historial resultante (`git log --oneline --graph --all`) hasta el final de la Parte D:

```
*   8348d49 Merge branch 'feat/filtro-por-prioridad' into main
|\
| * c783b9d feat(tasks): añade filtro de búsqueda por prioridad
|/
* 7b33d90 chore(editor): añade .editorconfig
* a2b5299 chore: proyecto Maven inicial de la API de tareas
```

## Parte E — Formateo automático con Spotless

Plugin `spotless-maven-plugin` (Google Java Format) añadido al `pom.xml`.

```
$ mvn spotless:check
[ERROR] ... Violations also present in 13 other files.
[ERROR] Run 'mvn spotless:apply' to fix these violations.
```

(el código se escribió con indentación de 4 espacios; Google Java Format exige 2 → `check` falla como se espera).

```
$ mvn spotless:apply   # reformatea
$ mvn spotless:check   # ahora pasa, exit 0
```

Hook de pre-commit versionado en `.githooks/pre-commit`, activado con `git config core.hooksPath .githooks`:

```sh
#!/bin/sh
echo "Formatting with Spotless..."
mvn -q spotless:apply || exit 1
git add -u
```

**Verificación del hook**: se desordenó a propósito la indentación de `TaskPriority.java` (mezcla de 8, 2 y 12 espacios) y se hizo `git commit`. El commit resultante ya contiene el archivo con la indentación correcta de Google Java Format (2 espacios), sin intervención manual — confirma que es imposible commitear código sin formatear mientras el hook esté activo.

> Nota: como el hook hace `git add -u`, re-añade **todos** los archivos ya trackeados que estén modificados en el working directory, no solo el que se había hecho `git add` explícitamente. Al ejecutar el hook con varios ficheros ya reformateados pendientes de commit, todo terminó en un único commit (`chore(build): añade Spotless para formateo automático`) en lugar de en los dos commits separados que sugiere el boletín — comportamiento real y esperable del script tal cual está escrito.

## Parte F — Conflicto de merge

Dos ramas creadas desde el mismo commit de `main`, modificando la misma línea (`server.port`) de `application.yml`:

- `feat/puerto-9090` → `port: 9090`
- `feat/puerto-9091` → `port: 9091`

```
$ git merge --no-ff feat/puerto-9090     # OK, sin conflicto

$ git merge --no-ff feat/puerto-9091
Auto-fusionando src/main/resources/application.yml
CONFLICTO (contenido): Conflicto de fusión en src/main/resources/application.yml
```

Contenido con marcadores:

```yaml
server:
<<<<<<< HEAD
  port: 9090
=======
  port: 9091
>>>>>>> feat/puerto-9091
```

Resuelto a mano eliminando los marcadores y dejando `port: 9090`, luego `git add` + `git commit`.

## Historial final completo

```
*   b72c7e6 Merge branch 'feat/puerto-9091' into main
|\
| * 530f82d feat(config): expone la API en el puerto 9091
* |   95058c5 Merge branch 'feat/puerto-9090' into main
|\ \
| |/
|/|
| * 986eb3d feat(config): expone la API en el puerto 9090
|/
* d678ab1 test: verifica que el hook de pre-commit formatea con Spotless
* 6d9bce5 chore(build): añade Spotless para formateo automático
*   8348d49 Merge branch 'feat/filtro-por-prioridad' into main
|\
| * c783b9d feat(tasks): añade filtro de búsqueda por prioridad
|/
* 7b33d90 chore(editor): añade .editorconfig
* a2b5299 chore: proyecto Maven inicial de la API de tareas
```

## Verificación final

```
$ mvn clean verify
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

$ ls target/*.jar
target/todo-api-0.1.0.jar   (49 MB, fat jar ejecutable)
```

Proyecto compila, pasa los 10 tests unitarios (reglas de negocio reales, sin tests de integración ni contexto Spring), se empaqueta como fat JAR y arranca con `java -jar`. Historial Git con commits Conventional Commits, rama fusionada, hook de Spotless activo y un conflicto real provocado y resuelto a mano.
