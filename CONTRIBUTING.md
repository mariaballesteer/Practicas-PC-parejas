# Cómo contribuir

Gracias por querer colaborar. Esta guía explica cómo poner en marcha el proyecto y cómo se trabaja en él.

## Equipo

Estas personas son colaboradoras del repositorio y propietarias del código (`.github/CODEOWNERS`), así que pueden revisar y aprobar Pull Requests:

- Maria Ballester (@mariaballesteer)
- Lucía Olmos Martínez (@luciaolmosmartinez)
- Nieves Turpín (@nievestg73)

Para añadir a alguien más: el propietario del repositorio le invita como colaborador en **Settings → Collaborators** con permiso de escritura, y se añade su usuario a `.github/CODEOWNERS` y a esta lista mediante un Pull Request.

## Poner en marcha el entorno

Necesitas Git, JDK 21 y Maven 3.9 o superior.

    git clone https://github.com/mariaballesteer/Practicas-PC-parejas.git
    cd Practicas-PC-parejas
    git config core.hooksPath .githooks
    mvn clean package

El `git config` activa el hook de pre-commit, que ejecuta Spotless (estilo de Google) y formatea el código antes de cada commit. Hay que hacerlo una vez después de clonar. El hook se puede saltar con `--no-verify`, pero no lo hagas: la comprobación de formato también es obligatoria para fusionar.

## Flujo de trabajo

1. Crea un issue con la plantilla que corresponda (mejora o error) y su etiqueta.
2. Crea una rama a partir de `main` actualizada, con un prefijo que indique el tipo de cambio: `feat/`, `fix/`, `chore/` o `docs/`. Por ejemplo, `feat/busqueda-por-estado`.
3. Haz el cambio y añade o ajusta los tests. Antes de subirlo, comprueba que `mvn clean package` termina en `BUILD SUCCESS`.
4. Sube la rama: `git push -u origin nombre-de-la-rama`.
5. Abre un Pull Request con la plantilla y enlaza el issue con `Closes #N`.
6. Atiende los comentarios de la revisión subiendo commits a la misma rama.
7. Cuando esté aprobado, se fusiona con squash y se borra la rama.

## Mensajes de commit y título del PR

Seguimos Conventional Commits: `tipo(ámbito): descripción en imperativo`. Los tipos habituales son `feat`, `fix`, `docs`, `test`, `refactor`, `chore` y `ci`. Ejemplo:

    feat(tareas): añade filtro de búsqueda por estado

## Revisión

- Todo el código pasa por un Pull Request; no se hace push directo a `main`.
- Hace falta al menos una aprobación de un propietario del código (`.github/CODEOWNERS`), y quien abre el PR no puede aprobar el suyo.
- Las conversaciones abiertas deben estar resueltas antes de fusionar.
- Si se suben commits nuevos, la aprobación anterior caduca y hay que volver a revisar.

## Política de fusión: squash

Los PR se fusionan con **Squash and merge**. Cada PR es un cambio con significado (un endpoint, un filtro, una validación), así que en `main` entra un único commit por PR. Los commits intermedios de la rama, como correcciones tras la revisión, no ensucian el historial. El commit resultante toma el título del PR, por eso el título debe seguir Conventional Commits.

## Poner tu rama al día

Si `main` avanza mientras tu PR sigue abierto, no fusiones `main` en tu rama: reaplica tus commits encima con un rebase.

    git switch nombre-de-la-rama
    git fetch origin
    git rebase origin/main

Si Git se detiene por un conflicto, edita los archivos marcados, resuélvelo y continúa:

    git add archivo-resuelto
    git rebase --continue

Al terminar, sube la rama reescrita. `--force-with-lease` solo sobrescribe si nadie más ha modificado la rama remota desde la última vez que la viste:

    git push --force-with-lease

## Si no tienes permisos de escritura

Haz un fork del repositorio, trabaja en una rama de tu fork y abre el Pull Request desde ahí hacia `main`. El resto del flujo es igual: issue, plantilla y revisión.