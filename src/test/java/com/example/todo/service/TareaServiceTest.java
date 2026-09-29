package com.example.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.todo.dto.TareaRequest;
import com.example.todo.exception.ParametroInvalidoException;
import com.example.todo.exception.ReglaNegocioException;
import com.example.todo.exception.TareaNoEncontradaException;
import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Prioridad;
import com.example.todo.model.Tarea;
import com.example.todo.repository.TareaRepositoryEnMemoria;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests unitarios de las reglas de negocio: sin contexto de Spring ni base de datos. */
class TareaServiceTest {

  // Reloj fijo: "hoy" es siempre 15/06/2026 a las 10:00
  private static final Clock RELOJ =
      Clock.fixed(Instant.parse("2026-06-15T10:00:00Z"), ZoneOffset.UTC);
  private static final LocalDate HOY = LocalDate.of(2026, 6, 15);

  private TareaRepositoryEnMemoria repositorio;
  private TareaService servicio;

  @BeforeEach
  void preparar() {
    repositorio = new TareaRepositoryEnMemoria();
    servicio = new TareaService(repositorio, RELOJ);
  }

  private static TareaRequest peticion(String titulo, Prioridad prioridad, LocalDate fechaLimite) {
    return new TareaRequest(titulo, "Descripción de prueba", prioridad, fechaLimite);
  }

  // ---------- crear ----------

  @Test
  void crear_conFechaLimitePasada_lanzaReglaNegocio() {
    TareaRequest ayer = peticion("Entregar informe", Prioridad.MEDIA, HOY.minusDays(1));

    assertThatThrownBy(() -> servicio.crear(ayer))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("fecha límite");
    assertThat(repositorio.buscarTodas()).isEmpty();
  }

  @Test
  void crear_conFechaLimiteHoy_seAcepta() {
    Tarea tarea = servicio.crear(peticion("Entregar informe", Prioridad.MEDIA, HOY));

    assertThat(tarea.getFechaLimite()).isEqualTo(HOY);
  }

  @Test
  void crear_prioridadAltaSinFechaLimite_lanzaReglaNegocio() {
    TareaRequest sinFecha = peticion("Arreglar servidor", Prioridad.ALTA, null);

    assertThatThrownBy(() -> servicio.crear(sinFecha))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("ALTA");
  }

  @Test
  void crear_conTituloDuplicadoIgnorandoMayusculas_lanzaReglaNegocio() {
    servicio.crear(peticion("Comprar pan", Prioridad.BAJA, null));
    TareaRequest duplicada = peticion("  COMPRAR PAN  ", Prioridad.BAJA, null);

    assertThatThrownBy(() -> servicio.crear(duplicada))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("Ya existe");
  }

  @Test
  void crear_conTituloDeTareaYaCompletada_sePermite() {
    Tarea primera = servicio.crear(peticion("Comprar pan", Prioridad.BAJA, null));
    servicio.cambiarEstado(primera.getId(), EstadoTarea.EN_PROGRESO);
    servicio.cambiarEstado(primera.getId(), EstadoTarea.COMPLETADA);

    assertThatCode(() -> servicio.crear(peticion("comprar pan", Prioridad.BAJA, null)))
        .doesNotThrowAnyException();
  }

  @Test
  void crear_tareaValida_quedaPendienteConTituloRecortadoYFechaDeCreacion() {
    Tarea tarea = servicio.crear(peticion("  Estudiar Spring  ", Prioridad.MEDIA, HOY.plusDays(7)));

    assertThat(tarea.getId()).isEqualTo(1L);
    assertThat(tarea.getTitulo()).isEqualTo("Estudiar Spring");
    assertThat(tarea.getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
    assertThat(tarea.getFechaCreacion()).isEqualTo(LocalDateTime.of(2026, 6, 15, 10, 0));
    assertThat(tarea.getFechaCompletada()).isNull();
  }

  // ---------- obtener / listar ----------

  @Test
  void obtener_conIdInexistente_lanzaTareaNoEncontrada() {
    assertThatThrownBy(() -> servicio.obtener(999L))
        .isInstanceOf(TareaNoEncontradaException.class)
        .hasMessageContaining("999");
  }

  @Test
  void listar_filtraPorEstadoYPrioridad() {
    Tarea a = servicio.crear(peticion("Tarea A", Prioridad.BAJA, null));
    servicio.crear(peticion("Tarea B", Prioridad.ALTA, HOY.plusDays(1)));
    servicio.crear(peticion("Tarea C", Prioridad.BAJA, null));
    servicio.cambiarEstado(a.getId(), EstadoTarea.EN_PROGRESO);

    List<Tarea> todas = servicio.listar(null, null);
    List<Tarea> bajasPendientes = servicio.listar(EstadoTarea.PENDIENTE, Prioridad.BAJA);
    List<Tarea> enProgreso = servicio.listar(EstadoTarea.EN_PROGRESO, null);

    assertThat(todas).hasSize(3);
    assertThat(bajasPendientes).extracting(Tarea::getTitulo).containsExactly("Tarea C");
    assertThat(enProgreso).extracting(Tarea::getTitulo).containsExactly("Tarea A");
  }

  @Test
  void listarVencidas_devuelveSoloTareasNoCompletadasConFechaAnteriorAHoy() {
    LocalDateTime creada = HOY.minusDays(30).atStartOfDay();
    repositorio.guardar(
        new Tarea("Vencida pendiente", null, Prioridad.MEDIA, HOY.minusDays(1), creada));
    Tarea vencidaCompletada =
        new Tarea("Vencida completada", null, Prioridad.MEDIA, HOY.minusDays(2), creada);
    vencidaCompletada.setEstado(EstadoTarea.COMPLETADA);
    repositorio.guardar(vencidaCompletada);
    repositorio.guardar(new Tarea("Vence hoy", null, Prioridad.MEDIA, HOY, creada));
    repositorio.guardar(new Tarea("Vence mañana", null, Prioridad.MEDIA, HOY.plusDays(1), creada));
    repositorio.guardar(new Tarea("Sin fecha", null, Prioridad.BAJA, null, creada));

    List<Tarea> vencidas = servicio.listarVencidas();

    assertThat(vencidas).extracting(Tarea::getTitulo).containsExactly("Vencida pendiente");
  }

  @Test
  void listarVencidas_sinTareas_devuelveListaVacia() {
    assertThat(servicio.listarVencidas()).isEmpty();
  }

  // ---------- buscar ----------

  @Test
  void buscar_encuentraCoincidenciasEnElTitulo() {
    servicio.crear(peticion("Estudiar Spring Boot", Prioridad.MEDIA, null));
    servicio.crear(peticion("Comprar pan", Prioridad.BAJA, null));

    List<Tarea> encontradas = servicio.buscar("spring");

    assertThat(encontradas).extracting(Tarea::getTitulo).containsExactly("Estudiar Spring Boot");
  }

  @Test
  void buscar_encuentraCoincidenciasEnLaDescripcion() {
    Tarea tarea =
        repositorio.guardar(
            new Tarea(
                "Tarea A",
                "Repasar el capítulo de Spring",
                Prioridad.MEDIA,
                null,
                HOY.atStartOfDay()));
    repositorio.guardar(new Tarea("Tarea B", null, Prioridad.MEDIA, null, HOY.atStartOfDay()));

    List<Tarea> encontradas = servicio.buscar("SPRING");

    assertThat(encontradas).extracting(Tarea::getId).containsExactly(tarea.getId());
  }

  @Test
  void buscar_sinCoincidencias_devuelveListaVacia() {
    servicio.crear(peticion("Comprar pan", Prioridad.BAJA, null));

    assertThat(servicio.buscar("inexistente")).isEmpty();
  }

  @Test
  void buscar_tareaConDescripcionNula_noLanzaExcepcion() {
    repositorio.guardar(
        new Tarea("Tarea sin descripción", null, Prioridad.MEDIA, null, HOY.atStartOfDay()));

    assertThatCode(() -> servicio.buscar("descripcion")).doesNotThrowAnyException();
  }

  @Test
  void buscar_conTextoVacio_lanzaParametroInvalido() {
    assertThatThrownBy(() -> servicio.buscar("   "))
        .isInstanceOf(ParametroInvalidoException.class)
        .hasMessageContaining("q");
  }

  @Test
  void buscar_conTextoNulo_lanzaParametroInvalido() {
    assertThatThrownBy(() -> servicio.buscar(null)).isInstanceOf(ParametroInvalidoException.class);
  }

  // ---------- cambiarEstado ----------

  @Test
  void cambiarEstado_dePendienteDirectoACompletada_noSePermite() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));

    assertThatThrownBy(() -> servicio.cambiarEstado(tarea.getId(), EstadoTarea.COMPLETADA))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("Transición");
    assertThat(servicio.obtener(tarea.getId()).getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
  }

  @Test
  void cambiarEstado_aCompletada_registraLaFechaDeCompletado() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));
    servicio.cambiarEstado(tarea.getId(), EstadoTarea.EN_PROGRESO);

    Tarea completada = servicio.cambiarEstado(tarea.getId(), EstadoTarea.COMPLETADA);

    assertThat(completada.getEstado()).isEqualTo(EstadoTarea.COMPLETADA);
    assertThat(completada.getFechaCompletada()).isEqualTo(LocalDateTime.of(2026, 6, 15, 10, 0));
  }

  @Test
  void cambiarEstado_superandoElLimiteDeTareasEnProgreso_lanzaReglaNegocio() {
    for (int i = 1; i <= TareaService.MAX_TAREAS_EN_PROGRESO; i++) {
      Tarea t = servicio.crear(peticion("Tarea " + i, Prioridad.MEDIA, null));
      servicio.cambiarEstado(t.getId(), EstadoTarea.EN_PROGRESO);
    }
    Tarea extra = servicio.crear(peticion("Tarea extra", Prioridad.MEDIA, null));

    assertThatThrownBy(() -> servicio.cambiarEstado(extra.getId(), EstadoTarea.EN_PROGRESO))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("en progreso");
    assertThat(servicio.obtener(extra.getId()).getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
  }

  // ---------- actualizar ----------

  @Test
  void actualizar_tareaCompletada_lanzaReglaNegocio() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));
    servicio.cambiarEstado(tarea.getId(), EstadoTarea.EN_PROGRESO);
    servicio.cambiarEstado(tarea.getId(), EstadoTarea.COMPLETADA);
    TareaRequest cambio = peticion("Nuevo título", Prioridad.BAJA, null);

    assertThatThrownBy(() -> servicio.actualizar(tarea.getId(), cambio))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("completada");
  }

  @Test
  void actualizar_tareaVencidaSinTocarLaFecha_seAcepta() {
    // Tarea creada en el pasado cuya fecha límite ya venció
    Tarea vieja =
        repositorio.guardar(
            new Tarea(
                "Tarea vieja",
                null,
                Prioridad.MEDIA,
                HOY.minusDays(3),
                HOY.minusDays(10).atStartOfDay()));

    Tarea actualizada =
        servicio.actualizar(
            vieja.getId(), peticion("Tarea vieja renombrada", Prioridad.MEDIA, HOY.minusDays(3)));

    assertThat(actualizada.getTitulo()).isEqualTo("Tarea vieja renombrada");
  }

  @Test
  void actualizar_conNuevaFechaLimitePasada_lanzaReglaNegocio() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, HOY.plusDays(5)));
    TareaRequest cambio = peticion("Tarea A", Prioridad.MEDIA, HOY.minusDays(1));

    assertThatThrownBy(() -> servicio.actualizar(tarea.getId(), cambio))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("fecha límite");
  }

  @Test
  void actualizar_conSuPropioTitulo_noSeConsideraDuplicado() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));

    Tarea actualizada =
        servicio.actualizar(tarea.getId(), peticion("Tarea A", Prioridad.ALTA, HOY.plusDays(2)));

    assertThat(actualizada.getPrioridad()).isEqualTo(Prioridad.ALTA);
  }

  // ---------- eliminar ----------

  @Test
  void eliminar_tareaEnProgreso_lanzaReglaNegocio() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));
    servicio.cambiarEstado(tarea.getId(), EstadoTarea.EN_PROGRESO);

    assertThatThrownBy(() -> servicio.eliminar(tarea.getId()))
        .isInstanceOf(ReglaNegocioException.class)
        .hasMessageContaining("en progreso");
    assertThat(repositorio.buscarPorId(tarea.getId())).isPresent();
  }

  @Test
  void eliminar_tareaPendiente_laBorraYDespuesNoSeEncuentra() {
    Tarea tarea = servicio.crear(peticion("Tarea A", Prioridad.MEDIA, null));

    servicio.eliminar(tarea.getId());

    assertThatThrownBy(() -> servicio.obtener(tarea.getId()))
        .isInstanceOf(TareaNoEncontradaException.class);
  }

  @Test
  void eliminar_idInexistente_lanzaTareaNoEncontrada() {
    assertThatThrownBy(() -> servicio.eliminar(404L))
        .isInstanceOf(TareaNoEncontradaException.class);
  }
}
