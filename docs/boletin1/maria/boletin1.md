# Boletín 1

## Resumen de commits
 
| # | Mensaje del commit | Qué se hizo | Autor | Enlace |
|---|--------------------|-------------|-------|--------|
| 1 | `chore: proyecto Maven inicial de la API de tareas` | Primer commit con la API de tareas generada con IA (24 archivos), más `.gitignore` y `.gitattributes`. | Maria Ballester | [3b41970](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/3b41970) |
| 2 | `chore(editor): añade .editorconfig` | Añade el `.editorconfig` del boletín en un commit aparte. | Maria Ballester | [5227e25](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/5227e25) |
| 3 | `feat(tareas): añade endpoint GET /api/tareas/vencidas` | Nuevo endpoint que lista las tareas no completadas cuya fecha límite ya pasó, con 2 tests. Hecho en una rama y fusionado en `main`. | Maria Ballester | [d3a85b4](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/d3a85b4) |
| 4 | `chore(build): añade Spotless para formateo automático` | Añade el plugin de Spotless al `pom.xml`. | Maria Ballester | [97e5d77](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/97e5d77) |
| 5 | `chore(format): aplica Spotless al código existente` | Reformatea los 20 archivos Java con `spotless:apply`. | Maria Ballester | [a76ac1f](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/a76ac1f) |
| 6 | `chore(hooks): añade hook de pre-commit con Spotless` | Añade `.githooks/pre-commit`, que ejecuta Spotless antes de cada commit. | Maria Ballester | [a187b4f](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/a187b4f) |
| 7 | `docs(readme): documenta cómo activar el hook de pre-commit` | Explica en el README que hay que ejecutar `git config core.hooksPath .githooks` tras clonar. | Maria Ballester | [43522db](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/43522db) |
| 8 | `docs(model): documenta la enumeración Prioridad` | Añade una Javadoc a `Prioridad`, dejando el código mal indentado a propósito para probar el hook. | Maria Ballester | [027743f](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/027743f) |
| 9 | `docs(model): lista los valores en la Javadoc de Prioridad` | Rama `docs/prioridad-a`: cambia la línea de Javadoc de `Prioridad`. | Maria Ballester | [85ef32d](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/85ef32d) |
| 10 | `docs(model): reescribe la Javadoc de Prioridad` | Rama `docs/prioridad-b`: cambia esa misma línea de otra forma. | Maria Ballester | [8b6d26f](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/8b6d26f) |
| 11 | `docs(model): resuelve el conflicto de la Javadoc de Prioridad` | Commit de fusión: resuelve a mano el conflicto entre las dos ramas. | Maria Ballester | [c806c77](https://github.com/mariaballesteer/Practicas-PC-parejas/commit/c806c77) |

Autoría comprobada con `git log`:

```text
$ git log --reverse --format='%h | %s | %an'
3b41970 | chore: proyecto Maven inicial de la API de tareas | Maria Ballester
5227e25 | chore(editor): añade .editorconfig | Maria Ballester
d3a85b4 | feat(tareas): añade endpoint GET /api/tareas/vencidas | Maria Ballester
97e5d77 | chore(build): añade Spotless para formateo automático | Maria Ballester
a76ac1f | chore(format): aplica Spotless al código existente | Maria Ballester
a187b4f | chore(hooks): añade hook de pre-commit con Spotless | Maria Ballester
43522db | docs(readme): documenta cómo activar el hook de pre-commit | Maria Ballester
027743f | docs(model): documenta la enumeración Prioridad | Maria Ballester
85ef32d | docs(model): lista los valores en la Javadoc de Prioridad | Maria Ballester
8b6d26f | docs(model): reescribe la Javadoc de Prioridad | Maria Ballester
c806c77 | docs(model): resuelve el conflicto de la Javadoc de Prioridad | Maria Ballester
```

## Detalle por commit

### 1. `chore: proyecto Maven inicial de la API de tareas`
- **Qué se hizo:** primer commit del repositorio, con el proyecto Maven de la API de tareas (24 archivos: código, tests, `pom.xml` y `README.md`) y los archivos `.gitignore` y `.gitattributes` del boletín.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte D, primer punto (inicializar el repositorio). El proyecto viene de la Parte B.
- **Uso de IA:** Claude. Le pegué el prompt que hay en el enunciado completo de la Parte B y me generó el proyecto entero de `To-Do API`. Lo compilé, pasé los tests y probé los endpoints con `curl` (Partes B y C del boletín).

### 2. `chore(editor): añade .editorconfig`
- **Qué se hizo:** creé el `.editorconfig` con el contenido exacto que da el boletín e hice un commit con este.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte D, punto del `.editorconfig`.
- **Uso de IA:** ninguno.

### 3. `feat(tareas): añade endpoint GET /api/tareas/vencidas`
- **Qué se hizo:** en la rama `feat/tareas-vencidas` añadí el endpoint `GET /api/tareas/vencidas`, que devuelve las tareas no completadas con fecha límite anterior a hoy. Son tres archivos (`TareaService`, `TareaController` y `TareaServiceTest`) y 34 líneas nuevas, con 2 tests. Antes de commitear ejecuté `mvn clean package` y salieron 43 tests en verde. Después fusioné la rama en `main`, y como `main` no se había movido fue un *fast-forward*.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte D, puntos de crear una rama, hacer un cambio no trivial y fusionarla.
- **Uso de IA:** Claude. Le pegué el enunciado de la Parte D y me propuso el endpoint y el código.

### 4. `chore(build): añade Spotless para formateo automático`
- **Qué se hizo:** añadí el plugin de Spotless al `pom.xml` y ejecuté `mvn spotless:check` antes de commitear, que falló en 20 archivos. Este commit lleva solo el `pom.xml`.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte E, primer punto.
- **Uso de IA:** ninguno.

### 5. `chore(format): aplica Spotless al código existente`
- **Qué se hizo:** ejecuté `mvn spotless:apply`, que reformateó los 20 archivos Java (797 inserciones y 754 borrados). Antes de commitear comprobé que `spotless:check` daba `BUILD SUCCESS` y que `mvn clean package` seguía pasando los 43 tests.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte E, punto de probar `check` y `apply`.
- **Uso de IA:** ninguno.

### 6. `chore(hooks): añade hook de pre-commit con Spotless`
- **Qué se hizo:** creé `.githooks/pre-commit`, lo hice ejecutable y configuré `git config core.hooksPath .githooks`. Al hacer este commit salió el mensaje `Formatting with Spotless...`, así que el hook ya se ejecutaba.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte E, punto del hook de pre-commit.
- **Uso de IA:** ninguno.

### 7. `docs(readme): documenta cómo activar el hook de pre-commit`
- **Qué se hizo:** añadí al `README.md` una sección corta sobre Spotless y sobre el `git config` que hay que ejecutar una vez tras clonar.
- **Autor:** Maria Ballester
- **Relación con el boletín:** recuadro OJO de la Parte E ("documéntalo en el README").
- **Uso de IA:** Claude. Escribió la sección del `README.md`.

### 8. `docs(model): documenta la enumeración Prioridad`
- **Qué se hizo:** sobrescribí `Prioridad.java` con la indentación desordenada y una Javadoc, y lo commiteé. El hook lo reformateó antes de crear el commit: `git show` muestra la Javadoc en una línea, el `public enum` limpio.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte E, último punto (comprobar que el hook funciona).
- **Uso de IA:** Claude. Me redactó el contenido desordenado para la prueba.

### 9. `docs(model): lista los valores en la Javadoc de Prioridad`
- **Qué se hizo:** en la rama `docs/prioridad-a` cambié la línea de Javadoc a `/** Niveles de prioridad de una tarea: BAJA, MEDIA o ALTA. */`.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte F, primer punto (dos ramas que tocan la misma línea).
- **Uso de IA:** ninguno.

### 10. `docs(model): reescribe la Javadoc de Prioridad`
- **Qué se hizo:** en la rama `docs/prioridad-b`, creada desde `main` antes de fusionar la A, cambié esa misma línea a `/** Prioridad que se asigna a cada tarea. */`.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte F, primer punto.
- **Uso de IA:** ninguno.

### 11. `docs(model): resuelve el conflicto de la Javadoc de Prioridad`
- **Qué se hizo:** fusioné la rama A en `main` (*fast-forward*) y después la B, que dio `CONFLICT (content)` en `Prioridad.java`. Edité el archivo a mano con nano, borré los marcadores `<<<<<<<`, `=======` y `>>>>>>>` y combiné las dos versiones en una sola línea de Javadoc: `/** Niveles de prioridad que se asigna a cada tarea: BAJA, MEDIA o ALTA. */`. Comprobé con `grep` que no quedaba ningún marcador y cerré con `git add` y `git commit`.
- **Autor:** Maria Ballester
- **Relación con el boletín:** Parte F, puntos segundo a cuarto.
- **Uso de IA:** Claude me dio los pasos y los comandos.
- **Resultado:**

```text
maria@maria:~/todo-api$ git show c806c77
commit c806c77e2ee71d3f2f81716529f939fafd6f8d6c (HEAD -> main)
Merge: 85ef32d 8b6d26f
Author: Maria Ballester <mariaballester.004@gmail.com>
Date:   Mon Sep 21 21:19:48 2026 +0200

    docs(model): resuelve el conflicto de la Javadoc de Prioridad

diff --cc src/main/java/com/example/todo/model/Prioridad.java
index c51b94b,bf6ba3e..318e576
--- a/src/main/java/com/example/todo/model/Prioridad.java
+++ b/src/main/java/com/example/todo/model/Prioridad.java
@@@ -1,6 -1,6 +1,6 @@@
  package com.example.todo.model;

- /** Niveles de prioridad de una tarea: BAJA, MEDIA o ALTA. */
 -/** Prioridad que se asigna a cada tarea. */
++/** Niveles de prioridad que se asigna a cada tarea: BAJA, MEDIA o ALTA. */
  public enum Prioridad {
    BAJA,
    MEDIA,
```

## Descripción narrativa de lo hecho en la sesión y relación con los commits

Las salidas de la terminal están recortadas para que se lean bien: quito las líneas de descarga de Maven y las repetidas, y lo marco con `[...]` cuando falta un trozo largo.

### Parte A

Trabajo en Ubuntu sobre WSL2. Después comprobé las versiones: OpenJDK 21.0.12, `javac` 21.0.12 y Maven 3.9.12, y Maven usa el JDK 21. Luego configuré mi identidad en Git y las opciones `pull.ff only` e `init.defaultBranch main`, y las verifiqué con `git config --global --list`.

Versiones:

```text
maria@maria:~$ java -version
javac -version
mvn -version
openjdk version "21.0.12" 2026-07-21
OpenJDK Runtime Environment (build 21.0.12+8-1-26.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12+8-1-26.04-Ubuntu, mixed mode, sharing)
javac 21.0.12
Apache Maven 3.9.12
Maven home: /usr/share/maven
Java version: 21.0.12, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.6.87.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

Configuración de Git:

```text
maria@maria:~$ git config --global user.name "Maria Ballester"
maria@maria:~$ git config --global user.email "mariaballester.004@gmail.com"
maria@maria:~$ git config --global pull.ff only
maria@maria:~$ git config --global init.defaultBranch main
maria@maria:~$ git config --global --list
user.name=Maria Ballester
user.email=mariaballester.004@gmail.com
pull.ff=only
init.defaultbranch=main
```

### Parte B

Le pasé a Claude el enunciado de esta parte y generó un proyecto Spring Boot 3.5 con Java 21 y Maven: una API de gestión de tareas.

En el proyecto la entidad `Tarea` tiene título, descripción, prioridad, estado, fecha límite, fecha de creación y fecha de completado. La API tiene el CRUD completo y está separada en controlador, servicio y repositorio. El repositorio es en memoria, sin base de datos. Los errores se gestionan en un `@RestControllerAdvice`: 404 si la tarea no existe, 422 si se incumple una regla de negocio y 400 si los datos no son válidos. Las reglas de negocio que hay son:

- La fecha límite no puede ser anterior a hoy.
- Una tarea de prioridad ALTA debe tener fecha límite.
- No puede haber dos tareas activas con el mismo título.
- Solo se permiten ciertas transiciones de estado (PENDIENTE, EN_PROGRESO, COMPLETADA).
- Como máximo hay 3 tareas en progreso a la vez.
- Una tarea completada no se puede modificar.
- Una tarea en progreso no se puede eliminar.

Tiene 41 tests unitarios, sin arrancar Spring ni base de datos, y `mvn package` da `BUILD SUCCESS`.

Primer `mvn package` del proyecto generado (salida recortada):

```text
maria@maria:~/todo-api$ mvn package
[INFO] Results:
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jar:3.4.2:jar (default-jar) @ todo-api ---
[INFO] Building jar: /home/maria/todo-api/target/todo-api.jar
[INFO] --- spring-boot:3.5.6:repackage (repackage) @ todo-api ---
[INFO] Replacing main artifact /home/maria/todo-api/target/todo-api.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] BUILD SUCCESS
```

### Parte C

Ejecuté `mvn clean package`, que dio `BUILD SUCCESS` con los 41 tests en verde, y comprobé el JAR con `ls -lh target/*.jar`: `todo-api.jar`, de 22 MB por llevar todas las dependencias dentro. Lo arranqué con `java -jar target/todo-api.jar` y Tomcat quedó escuchando en el puerto 8080.

Desde otra terminal probé la API con `curl`:

- `POST /api/tareas`: `201` con la cabecera `Location: /api/tareas/1`.
- `GET /api/tareas/1` y `GET /api/tareas`: `200` con la tarea creada.
- `PATCH /api/tareas/1/estado` a `EN_PROGRESO`: `200`.
- `GET /api/tareas/999`: `404` con un JSON de error, sin traza.
- `POST` con fecha límite de 2020: `422` con "La fecha límite no puede ser anterior a hoy".
- `DELETE` con la tarea en progreso: `422`. Después la devolví a `PENDIENTE` y el `DELETE` dio `204`.

`mvn clean package` y tamaño del JAR (salida recortada):

```text
maria@maria:~/todo-api$ mvn clean package
[INFO] Results:
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jar:3.4.2:jar (default-jar) @ todo-api ---
[INFO] Building jar: /home/maria/todo-api/target/todo-api.jar
[INFO] --- spring-boot:3.5.6:repackage (repackage) @ todo-api ---
[INFO] Replacing main artifact /home/maria/todo-api/target/todo-api.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] BUILD SUCCESS

maria@maria:~/todo-api$ ls -lh target/*.jar
-rw-r--r-- 1 maria maria 22M Sep 21 19:32 target/todo-api.jar
```

Arranque con `java -jar` (Tomcat en el puerto 8080):

```text
maria@maria:~/todo-api$ java -jar target/todo-api.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.6)

2026-09-21T19:35:24.423+02:00  INFO 3187 --- [           main] com.example.todo.TodoApiApplication      : Starting TodoApiApplication v1.0.0 using Java 21.0.12 with PID 3187 (/home/maria/todo-api/target/todo-api.jar started by maria in /home/maria/todo-api)
2026-09-21T19:35:24.425+02:00  INFO 3187 --- [           main] com.example.todo.TodoApiApplication      : No active profile set, falling back to 1 default profile: "default"
2026-09-21T19:35:25.411+02:00  INFO 3187 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-09-21T19:35:25.423+02:00  INFO 3187 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-09-21T19:35:25.423+02:00  INFO 3187 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.46]
2026-09-21T19:35:25.451+02:00  INFO 3187 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-09-21T19:35:25.451+02:00  INFO 3187 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 985 ms
2026-09-21T19:35:25.867+02:00  INFO 3187 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-09-21T19:35:25.878+02:00  INFO 3187 --- [           main] com.example.todo.TodoApiApplication      : Started TodoApiApplication in 1.847 seconds (process running for 2.249)
```

Pruebas con `curl` desde otra terminal:

```text
maria@maria:~$ curl -i -X POST localhost:8080/api/tareas -H "Content-Type: application/json" \
  -d '{"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","fechaLimite":"2030-12-31"}'
HTTP/1.1 201
Location: /api/tareas/1
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:36:27 GMT

{"id":1,"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","estado":"PENDIENTE","fechaLimite":"2030-12-31","fechaCreacion":"2026-09-21T19:36:27.167862048","fechaCompletada":null}
maria@maria:~$ curl -i localhost:8080/api/tareas/1
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:37:21 GMT

{"id":1,"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","estado":"PENDIENTE","fechaLimite":"2030-12-31","fechaCreacion":"2026-09-21T19:36:27.167862048","fechaCompletada":null}maria@maria:~$
maria@maria:~$ curl localhost:8080/api/tareas
[{"id":1,"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","estado":"PENDIENTE","fechaLimite":"2030-12-31","fechaCreacion":"2026-09-21T19:36:27.167862048","fechaCompletada":null}]maria@maria:~$
maria@maria:~$ curl -i -X PATCH localhost:8080/api/tareas/1/estado -H "Content-Type: application/json" \
  -d '{"estado":"EN_PROGRESO"}'
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:37:48 GMT

{"id":1,"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","estado":"EN_PROGRESO","fechaLimite":"2030-12-31","fechaCreacion":"2026-09-21T19:36:27.167862048","fechaCompletada":null}maria@maria:~$
maria@maria:~$
# Recurso inexistente (404 con JSON, sin traza)
curl -i localhost:8080/api/tareas/999
HTTP/1.1 404
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:38:01 GMT

{"timestamp":"2026-09-21T19:38:01.953584938","status":404,"error":"Not Found","mensaje":"No existe ninguna tarea con id 999","detalles":[]}maria@maria:~$
maria@maria:~$ curl -i -X POST localhost:8080/api/tareas -H "Content-Type: application/json" \
  -d '{"titulo":"Tarea antigua","prioridad":"BAJA","fechaLimite":"2020-01-01"}'
HTTP/1.1 422
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:38:19 GMT

{"timestamp":"2026-09-21T19:38:19.058302038","status":422,"error":"Unprocessable Entity","mensaje":"La fecha límite no puede ser anterior a hoy","detalles":[]}maria@maria:~$
maria@maria:~$ curl -i -X DELETE localhost:8080/api/tareas/1
curl -X PATCH localhost:8080/api/tareas/1/estado -H "Content-Type: application/json" -d '{"estado":"PENDIENTE"}'
curl -i -X DELETE localhost:8080/api/tareas/1
HTTP/1.1 422
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 17:38:39 GMT

{"timestamp":"2026-09-21T19:38:39.441190441","status":422,"error":"Unprocessable Entity","mensaje":"No se puede eliminar una tarea que está en progreso","detalles":[]}{"id":1,"titulo":"Estudiar Spring","descripcion":"Capítulo 3","prioridad":"ALTA","estado":"PENDIENTE","fechaLimite":"2030-12-31","fechaCreacion":"2026-09-21T19:36:27.167862048","fechaCompletada":null}HTTP/1.1 204
Date: Mon, 21 Sep 2026 17:38:39 GMT
```

### Parte D

Inicialicé el repositorio con `git init` y creé `.gitignore` y `.gitattributes` como pide el boletín. Antes del primer commit comprobé con `git status` que `target/` no aparecía. Hice el primer commit (commit 1) y después el `.editorconfig` por separado (commit 2).

Después creé la rama `feat/tareas-vencidas` y añadí el endpoint `GET /api/tareas/vencidas`, con sus tests (commit 3). Comprobé con `mvn clean package` (43 tests) y con `curl` que devolvía `200` y `[]` (la aplicación arranca vacía). Fusioné la rama en `main`, y como `main` no había avanzado Git hizo un *fast-forward*, por lo que el historial quedó en línea recta.

Rama `feat/tareas-vencidas`, cambios, `mvn clean package` (recortado) y prueba del endpoint:

```text
maria@maria:~/todo-api$ git switch -c feat/tareas-vencidas
Switched to a new branch 'feat/tareas-vencidas'
maria@maria:~/todo-api$ git branch --show-current
feat/tareas-vencidas
maria@maria:~/todo-api$ nano src/main/java/com/example/todo/service/TareaService.java
maria@maria:~/todo-api$ nano src/main/java/com/example/todo/controller/TareaController.java
maria@maria:~/todo-api$ nano src/test/java/com/example/todo/service/TareaServiceTest.java
maria@maria:~/todo-api$ git diff --stat
 .../todo/controller/TareaController.java     |  5 +++++
 .../example/todo/service/TareaService.java   |  8 +++++++
 .../todo/service/TareaServiceTest.java       | 21 ++++++++++++++++++
 3 files changed, 34 insertions(+)
maria@maria:~/todo-api$ mvn clean package
[INFO] Results:
[INFO] Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jar:3.4.2:jar (default-jar) @ todo-api ---
[INFO] Building jar: /home/maria/todo-api/target/todo-api.jar
[INFO] --- spring-boot:3.5.6:repackage (repackage) @ todo-api ---
[INFO] Replacing main artifact /home/maria/todo-api/target/todo-api.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] BUILD SUCCESS

maria@maria:~$ curl -i localhost:8080/api/tareas/vencidas
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2026 18:05:24 GMT
[]
```

Commit, fusión en `main` y grafo:

```text
maria@maria:~/todo-api$ git status
On branch feat/tareas-vencidas
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   src/main/java/com/example/todo/controller/TareaController.java
        modified:   src/main/java/com/example/todo/service/TareaService.java
        modified:   src/test/java/com/example/todo/service/TareaServiceTest.java

no changes added to commit (use "git add" and/or "git commit -a")
maria@maria:~/todo-api$ git add .
git commit -m "feat(tareas): añade endpoint GET /api/tareas/vencidas"
git switch main
git merge feat/tareas-vencidas
git log --oneline --graph --all
[feat/tareas-vencidas d3a85b4] feat(tareas): añade endpoint GET /api/tareas/vencidas
 3 files changed, 34 insertions(+)
Switched to branch 'main'
Updating 5227e25..d3a85b4
Fast-forward
 .../todo/controller/TareaController.java     |  5 +++++
 .../example/todo/service/TareaService.java   |  8 +++++++
 .../todo/service/TareaServiceTest.java       | 21 ++++++++++++++++++
 3 files changed, 34 insertions(+)
* d3a85b4 (HEAD -> main, feat/tareas-vencidas) feat(tareas): añade endpoint GET /api/tareas/vencidas
* 5227e25 chore(editor): añade .editorconfig
* 3b41970 chore: proyecto Maven inicial de la API de tareas
```

### Parte E

Añadí Spotless al `pom.xml` con la configuración del boletín. `mvn spotless:check` falló en 20 archivos, como era de esperar. Commiteé el `pom.xml` solo (commit 4) y después ejecuté `mvn spotless:apply`. Tras aplicarlo, `check` dio `BUILD SUCCESS` y `mvn clean package` mantuvo los 43 tests en verde. Ese reformateo lo commiteé aparte (commit 5). Separé el plugin (commit 4), el reformateo (commit 5) y el hook (commit 6) en diferentes commits.

Luego creé el hook, lo hice ejecutable y lo activé con `git config core.hooksPath .githooks`. Al commitearlo salió `Formatting with Spotless...`. Además, documenté el `git config` en el README (commit 7). Por último probé el hook (commit 8): dejé `Prioridad.java` con la indentación mal y una Javadoc, hice commit, y `git show` muestra que el commit ya tiene el código formateado, y `git status` y `spotless:check` quedaron limpios.

`spotless:check` falla y commit del plugin:

```text
maria@maria:~/todo-api$ git status
On branch main
nothing to commit, working tree clean
maria@maria:~/todo-api$ nano pom.xml
maria@maria:~/todo-api$ mvn spotless:check
[...]
[INFO] Index file does not exist. Fallback to an empty index
[INFO] Spotless.Java is keeping 20 files clean - 20 needs changes to be clean, 0 were already clean, 0 were skipped because caching determined they were already clean
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.495 s
[INFO] Finished at: 2026-09-21T20:54:59+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.diffplug.spotless:spotless-maven-plugin:2.43.0:check (default-cli) on project todo-api: The following files had format violations:
[ERROR]     src/test/java/com/example/todo/dto/TareaRequestValidacionTest.java
[ERROR]         @@ -2,72 +2,71 @@
[ERROR]
[ERROR]          import·static·org.assertj.core.api.Assertions.assertThat;
[ERROR]
[ERROR]         +import·com.example.todo.model.Prioridad;
[ERROR]         +import·jakarta.validation.ConstraintViolation;
[ERROR]         +import·jakarta.validation.Validation;
[ERROR]         +import·jakarta.validation.Validator;
[ERROR]          import·java.time.LocalDate;
[ERROR]          import·java.util.List;
[ERROR]          import·java.util.Set;
[ERROR]         -
[ERROR]          import·org.junit.jupiter.api.BeforeAll;
[ERROR]          import·org.junit.jupiter.api.Test;
[ERROR]
[ERROR]         -import·com.example.todo.model.Prioridad;
[ERROR]         -
[ERROR]         -import·jakarta.validation.ConstraintViolation;
[ERROR]         -import·jakarta.validation.Validation;
[ERROR]         -import·jakarta.validation.Validator;
[ERROR]         -
[ERROR]          /**·Comprueba·las·validaciones·de·Bean·Validation·sin·arrancar·Spring.·*/
[ERROR]          class·TareaRequestValidacionTest·{
[ERROR]
[ERROR]         -····private·static·Validator·validador;
[ERROR]         +··private·static·Validator·validador;
[ERROR]
[ERROR]         -····@BeforeAll
[ERROR]         -····static·void·crearValidador()·{
[ERROR]         -········validador·=·Validation.buildDefaultValidatorFactory().getValidator();
[ERROR]         -····}
[ERROR]         +··@BeforeAll
[ERROR]         +··static·void·crearValidador()·{
[ERROR]         +····validador·=·Validation.buildDefaultValidatorFactory().getValidator();
[ERROR]         +··}
[ERROR]
[ERROR]         -····private·static·List<String>·camposInvalidos(TareaRequest·peticion)·{
[ERROR]         -········Set<ConstraintViolation<TareaRequest>>·violaciones·=·validador.validate(peticion);
[ERROR]         -········return·violaciones.stream().map(v·->·v.getPropertyPath().toString()).sorted().toList();
[ERROR]         -····}
[ERROR]         +··private·static·List<String>·camposInvalidos(TareaRequest·peticion)·{
[ERROR]         +····Set<ConstraintViolation<TareaRequest>>·violaciones·=·validador.validate(peticion);
[ERROR]         +····return·violaciones.stream().map(v·->·v.getPropertyPath().toString()).sorted().toList();
[ERROR]         +··}
[ERROR]
[ERROR]         -····@Test
[ERROR]         -····void·peticionCorrecta_noTieneViolaciones()·{
[ERROR]         -········TareaRequest·ok·=·new·TareaRequest("Estudiar",·"Repasar·capítulo·3",·Prioridad.MEDIA,·LocalDate.of(2030,·1,·1));
[ERROR]         +··@Test
[ERROR]     ... (71 more lines that didn't fit)
[ERROR] Violations also present in 19 other files.
[ERROR] Run 'mvn spotless:apply' to fix these violations.

maria@maria:~/todo-api$ git add pom.xml
git commit -m "chore(build): añade Spotless para formateo automático"
[main 97e5d77] chore(build): añade Spotless para formateo automático
 1 file changed, 14 insertions(+), 1 deletion(-)
```

`spotless:apply`, `check`, `mvn clean package` y commit del reformateo (recortado):

```text
maria@maria:~/todo-api$ mvn spotless:apply
git diff --stat
mvn spotless:check
[INFO] --- spotless:2.43.0:apply (default-cli) @ todo-api ---
[INFO] Writing clean file: /home/maria/todo-api/src/test/java/com/example/todo/dto/TareaRequestValidacionTest.java
[INFO] Writing clean file: /home/maria/todo-api/src/test/java/com/example/todo/repository/TareaRepositoryEnMemoriaTest.java
[INFO] Writing clean file: /home/maria/todo-api/src/test/java/com/example/todo/exception/GlobalExceptionHandlerTest.java
[INFO] Writing clean file: /home/maria/todo-api/src/test/java/com/example/todo/service/TareaServiceTest.java
[INFO] Writing clean file: /home/maria/todo-api/src/test/java/com/example/todo/model/EstadoTareaTest.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/dto/TareaResponse.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/dto/ErrorRespuesta.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/dto/TareaRequest.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/dto/CambioEstadoRequest.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/repository/TareaRepositoryEnMemoria.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/repository/TareaRepository.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/controller/TareaController.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/exception/TareaNoEncontradaException.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/exception/GlobalExceptionHandler.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/exception/ReglaNegocioException.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/service/TareaService.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/model/Prioridad.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/model/EstadoTarea.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/model/Tarea.java
[INFO] Writing clean file: /home/maria/todo-api/src/main/java/com/example/todo/TodoApiApplication.java
[INFO] Spotless.Java is keeping 20 files clean - 20 were changed to be clean, 0 were already clean, 0 were skipped because caching determined they were already clean
[INFO] BUILD SUCCESS
maria@maria:~/todo-api$ mvn clean package
[INFO] Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jar:3.4.2:jar (default-jar) @ todo-api ---
[INFO] --- spring-boot:3.5.6:repackage (repackage) @ todo-api ---
[INFO] BUILD SUCCESS
[INFO] Total time:  4.305 s
maria@maria:~/todo-api$ git add -u
git commit -m "chore(format): aplica Spotless al código existente"
[main a76ac1f] chore(format): aplica Spotless al código existente
 20 files changed, 797 insertions(+), 754 deletions(-)
```

Creación del hook, activación y commit (sale `Formatting with Spotless...`):

```text
maria@maria:~/todo-api$ mkdir -p .githooks
maria@maria:~/todo-api$ cat > .githooks/pre-commit <<'EOF'
#!/bin/sh
echo "Formatting with Spotless..."
mvn -q spotless:apply || exit 1
git add -u          # re-añade los archivos que Spotless haya reformateado
EOF
maria@maria:~/todo-api$ chmod +x .githooks/pre-commit
maria@maria:~/todo-api$ git config core.hooksPath .githooks
maria@maria:~/todo-api$ git config --get core.hooksPath
.githooks
maria@maria:~/todo-api$ git add .githooks/pre-commit
git commit -m "chore(hooks): añade hook de pre-commit con Spotless"
Formatting with Spotless...
[main a187b4f] chore(hooks): añade hook de pre-commit con Spotless
 1 file changed, 4 insertions(+)
 create mode 100755 .githooks/pre-commit
```

Commit del README:

```text
maria@maria:~/todo-api$ cat >> README.md <<'EOF'

## Formato del código

Uso Spotless (con el estilo de Google) para que todo el código tenga el mismo formato.

- `mvn spotless:check` comprueba si el código está bien formateado.
- `mvn spotless:apply` lo formatea.

Hay un hook de pre-commit en `.githooks/` que ejecuta `spotless:apply` antes de cada commit.
Después de clonar el repositorio hay que activarlo una vez con:

    git config core.hooksPath .githooks
EOF
```

Prueba del hook con `Prioridad.java` desordenado, `git show` y `git status`:

```text
maria@maria:~/todo-api$ cat > src/main/java/com/example/todo/model/Prioridad.java <<'EOF'
package com.example.todo.model;

      /**   Niveles de prioridad de una tarea.   */
public   enum Prioridad {
          BAJA,   MEDIA,     ALTA
}
EOF

git add src/main/java/com/example/todo/model/Prioridad.java
git commit -m "docs(model): documenta la enumeración Prioridad"
Formatting with Spotless...
[main 027743f] docs(model): documenta la enumeración Prioridad
 1 file changed, 1 insertion(+)
maria@maria:~/todo-api$ git show
git status
mvn spotless:check
commit 027743f70426b3c5c8318ca476db32b20131544d (HEAD -> main)
Author: Maria Ballester <mariaballester.004@gmail.com>
Date:   Mon Sep 21 21:11:07 2026 +0200

    docs(model): documenta la enumeración Prioridad

diff --git a/src/main/java/com/example/todo/model/Prioridad.java b/src/main/java/com/example/todo/model/Prioridad.java
index 1ecd8e3..d479737 100644
--- a/src/main/java/com/example/todo/model/Prioridad.java
+++ b/src/main/java/com/example/todo/model/Prioridad.java
@@ -1,5 +1,6 @@
 package com.example.todo.model;

+/** Niveles de prioridad de una tarea. */
 public enum Prioridad {
   BAJA,
   MEDIA,
On branch main
nothing to commit, working tree clean
[...]
[INFO] Spotless.Java is keeping 20 files clean - 0 needs changes to be clean, 0 were already clean, 20 were skipped because caching determined they were already clean
[INFO] BUILD SUCCESS
```

### Parte F

Creé dos ramas desde `main`, `docs/prioridad-a` y `docs/prioridad-b`, y en cada una cambié la misma línea de Javadoc de `Prioridad.java` de forma distinta (commits 9 y 10). Fusioné la A en `main`, que fue un *fast-forward*, y al fusionar la B Git avisó de `CONFLICT (content)`. `git status` marcó el archivo como `both modified` y el archivo tenía los marcadores con las dos versiones de la línea.

Lo resolví a mano con nano, borrando los marcadores y combinando las dos versiones en una sola línea. Comprobé con `grep` que no quedaba ninguno y cerré con `git add` y `git commit` (commit 11). El `git log --oneline --graph --all` final muestra la bifurcación de las dos ramas y el commit de fusión que las une.

Fusiones y conflicto:

```text
maria@maria:~/todo-api$ git switch main
git merge docs/prioridad-a
Switched to branch 'main'
Updating 027743f..85ef32d
Fast-forward
 src/main/java/com/example/todo/model/Prioridad.java | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
maria@maria:~/todo-api$ git merge docs/prioridad-b
Auto-merging src/main/java/com/example/todo/model/Prioridad.java
CONFLICT (content): Merge conflict in src/main/java/com/example/todo/model/Prioridad.java
Automatic merge failed; fix conflicts and then commit the result.
maria@maria:~/todo-api$ git status
cat src/main/java/com/example/todo/model/Prioridad.java
On branch main
You have unmerged paths.
  (fix conflicts and run "git commit")
  (use "git merge --abort" to abort the merge)

Unmerged paths:
  (use "git add <file>..." to mark resolution)
        both modified:   src/main/java/com/example/todo/model/Prioridad.java

no changes added to commit (use "git add" and/or "git commit -a")
package com.example.todo.model;

<<<<<<< HEAD
/** Niveles de prioridad de una tarea: BAJA, MEDIA o ALTA. */
=======
/** Prioridad que se asigna a cada tarea. */
>>>>>>> docs/prioridad-b
public enum Prioridad {
  BAJA,
  MEDIA,
  ALTA
}
```

Resolución del conflicto y commit de fusión:

```text
maria@maria:~/todo-api$ nano src/main/java/com/example/todo/model/Prioridad.java
maria@maria:~/todo-api$ grep -n '<<<<<<<\|=======\|>>>>>>>' src/main/java/com/example/todo/model/Prioridad.java
maria@maria:~/todo-api$ git add src/main/java/com/example/todo/model/Prioridad.java
git commit -m "docs(model): resuelve el conflicto de la Javadoc de Prioridad"
Formatting with Spotless...
[main c806c77] docs(model): resuelve el conflicto de la Javadoc de Prioridad
```

Grafo final:

```text
maria@maria:~/todo-api$ git log --oneline --graph --all
git status
*   c806c77 (HEAD -> main) docs(model): resuelve el conflicto de la Javadoc de Prioridad
|\
| * 8b6d26f (docs/prioridad-b) docs(model): reescribe la Javadoc de Prioridad
* | 85ef32d (docs/prioridad-a) docs(model): lista los valores en la Javadoc de Prioridad
|/
* 027743f docs(model): documenta la enumeración Prioridad
* 43522db docs(readme): documenta cómo activar el hook de pre-commit
* a187b4f chore(hooks): añade hook de pre-commit con Spotless
* a76ac1f chore(format): aplica Spotless al código existente
* 97e5d77 chore(build): añade Spotless para formateo automático
* d3a85b4 (feat/tareas-vencidas) feat(tareas): añade endpoint GET /api/tareas/vencidas
* 5227e25 chore(editor): añade .editorconfig
* 3b41970 chore: proyecto Maven inicial de la API de tareas
On branch main
nothing to commit, working tree clean
```

## Uso de IA

Herramienta: Claude.
- **Parte B:** generó el proyecto completo a partir del enunciado.
- **Parte D:** me propuso el endpoint `/vencidas` y su código.
- **Parte E:** redactó el texto de la sección del README.
- **Este documento:** Claude realizó la estructura a partir del enunciado y de las salidas de la terminal.

Todos los commits los hice yo con mi nombre y ninguno lo ha hecho la IA.
