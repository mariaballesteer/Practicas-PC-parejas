package com.example.todo.service;

import com.example.todo.dto.TareaRequest;
import com.example.todo.exception.ParametroInvalidoException;
import com.example.todo.exception.ReglaNegocioException;
import com.example.todo.exception.TareaNoEncontradaException;
import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Prioridad;
import com.example.todo.model.Tarea;
import com.example.todo.repository.TareaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio: 1. La fecha límite no puede ser anterior a hoy. 2. Una tarea de prioridad ALTA
 * debe tener fecha límite. 3. No puede haber dos tareas activas (no completadas) con el mismo
 * título. 4. Solo se permiten ciertas transiciones de estado (ver EstadoTarea). 5. Como máximo
 * MAX_TAREAS_EN_PROGRESO tareas en progreso a la vez. 6. Una tarea completada no se puede
 * modificar. 7. Una tarea en progreso no se puede eliminar.
 */
@Service
public class TareaService {

  public static final int MAX_TAREAS_EN_PROGRESO = 3;

  private final TareaRepository repositorio;
  private final Clock reloj;

  public TareaService(TareaRepository repositorio, Clock reloj) {
    this.repositorio = repositorio;
    this.reloj = reloj;
  }

  public Tarea crear(TareaRequest peticion) {
    validarFechaLimiteNoPasada(peticion.fechaLimite());
    validarPrioridadAltaConFecha(peticion.prioridad(), peticion.fechaLimite());
    String titulo = peticion.titulo().trim();
    validarTituloUnico(titulo, null);

    Tarea tarea =
        new Tarea(
            titulo,
            peticion.descripcion(),
            peticion.prioridad(),
            peticion.fechaLimite(),
            LocalDateTime.now(reloj));
    return repositorio.guardar(tarea);
  }

  public Tarea obtener(Long id) {
    return repositorio.buscarPorId(id).orElseThrow(() -> new TareaNoEncontradaException(id));
  }

  public List<Tarea> listar(EstadoTarea estado, Prioridad prioridad) {
    return repositorio.buscarTodas().stream()
        .filter(t -> estado == null || t.getEstado() == estado)
        .filter(t -> prioridad == null || t.getPrioridad() == prioridad)
        .toList();
  }

  public List<Tarea> listarVencidas() {
    LocalDate hoy = LocalDate.now(reloj);
    return repositorio.buscarTodas().stream()
        .filter(t -> t.getEstado() != EstadoTarea.COMPLETADA)
        .filter(t -> t.getFechaLimite() != null && t.getFechaLimite().isBefore(hoy))
        .toList();
  }

  public List<Tarea> buscar(String texto) {
    if (texto == null || texto.isBlank()) {
      throw new ParametroInvalidoException(
          "El parámetro 'q' es obligatorio y no puede estar vacío");
    }
    String textoNormalizado = texto.trim().toLowerCase();
    return repositorio.buscarTodas().stream()
        .filter(
            t ->
                contieneTexto(t.getTitulo(), textoNormalizado)
                    || contieneTexto(t.getDescripcion(), textoNormalizado))
        .toList();
  }

  private boolean contieneTexto(String campo, String textoNormalizado) {
    return campo != null && campo.toLowerCase().contains(textoNormalizado);
  }

  public Tarea actualizar(Long id, TareaRequest peticion) {
    Tarea tarea = obtener(id);
    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
      throw new ReglaNegocioException("No se puede modificar una tarea completada");
    }
    // Solo se valida la fecha si cambia: una tarea que venció puede seguir editándose
    if (!Objects.equals(peticion.fechaLimite(), tarea.getFechaLimite())) {
      validarFechaLimiteNoPasada(peticion.fechaLimite());
    }
    validarPrioridadAltaConFecha(peticion.prioridad(), peticion.fechaLimite());
    String titulo = peticion.titulo().trim();
    validarTituloUnico(titulo, id);

    tarea.actualizarDatos(
        titulo, peticion.descripcion(), peticion.prioridad(), peticion.fechaLimite());
    return repositorio.guardar(tarea);
  }

  public Tarea cambiarEstado(Long id, EstadoTarea nuevoEstado) {
    Tarea tarea = obtener(id);
    EstadoTarea actual = tarea.getEstado();

    if (!actual.puedeTransitarA(nuevoEstado)) {
      throw new ReglaNegocioException(
          "Transición de estado no permitida: " + actual + " -> " + nuevoEstado);
    }
    if (nuevoEstado == EstadoTarea.EN_PROGRESO
        && repositorio.contarPorEstado(EstadoTarea.EN_PROGRESO) >= MAX_TAREAS_EN_PROGRESO) {
      throw new ReglaNegocioException(
          "No puede haber más de " + MAX_TAREAS_EN_PROGRESO + " tareas en progreso a la vez");
    }

    tarea.setEstado(nuevoEstado);
    if (nuevoEstado == EstadoTarea.COMPLETADA) {
      tarea.setFechaCompletada(LocalDateTime.now(reloj));
    }
    return repositorio.guardar(tarea);
  }

  public void eliminar(Long id) {
    Tarea tarea = obtener(id);
    if (tarea.getEstado() == EstadoTarea.EN_PROGRESO) {
      throw new ReglaNegocioException("No se puede eliminar una tarea que está en progreso");
    }
    repositorio.eliminarPorId(id);
  }

  // ---------- validaciones privadas ----------

  private void validarFechaLimiteNoPasada(LocalDate fechaLimite) {
    if (fechaLimite != null && fechaLimite.isBefore(LocalDate.now(reloj))) {
      throw new ReglaNegocioException("La fecha límite no puede ser anterior a hoy");
    }
  }

  private void validarPrioridadAltaConFecha(Prioridad prioridad, LocalDate fechaLimite) {
    if (prioridad == Prioridad.ALTA && fechaLimite == null) {
      throw new ReglaNegocioException("Las tareas de prioridad ALTA deben tener fecha límite");
    }
  }

  private void validarTituloUnico(String titulo, Long idExcluido) {
    boolean duplicado =
        repositorio.buscarTodas().stream()
            .filter(t -> t.getEstado() != EstadoTarea.COMPLETADA)
            .filter(t -> !t.getId().equals(idExcluido))
            .anyMatch(t -> t.getTitulo().equalsIgnoreCase(titulo));
    if (duplicado) {
      throw new ReglaNegocioException("Ya existe una tarea activa con el título '" + titulo + "'");
    }
  }
}
