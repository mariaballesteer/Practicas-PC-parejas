package com.example.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Prioridad;
import com.example.todo.model.Tarea;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TareaRepositoryEnMemoriaTest {

  private TareaRepositoryEnMemoria repositorio;

  @BeforeEach
  void preparar() {
    repositorio = new TareaRepositoryEnMemoria();
  }

  private Tarea nueva(String titulo) {
    return new Tarea(titulo, null, Prioridad.MEDIA, null, LocalDateTime.of(2026, 1, 1, 0, 0));
  }

  @Test
  void guardar_asignaIdsCorrelativos() {
    Tarea a = repositorio.guardar(nueva("A"));
    Tarea b = repositorio.guardar(nueva("B"));

    assertThat(a.getId()).isEqualTo(1L);
    assertThat(b.getId()).isEqualTo(3L);
  }

  @Test
  void guardar_tareaExistente_noCambiaSuIdNiDuplica() {
    Tarea a = repositorio.guardar(nueva("A"));
    a.setEstado(EstadoTarea.EN_PROGRESO);

    repositorio.guardar(a);

    assertThat(repositorio.buscarTodas()).hasSize(1);
    assertThat(repositorio.buscarPorId(1L).orElseThrow().getEstado())
        .isEqualTo(EstadoTarea.EN_PROGRESO);
  }

  @Test
  void buscarPorId_inexistente_devuelveVacio() {
    assertThat(repositorio.buscarPorId(99L)).isEmpty();
  }

  @Test
  void eliminarPorId_quitaLaTarea() {
    Tarea a = repositorio.guardar(nueva("A"));
    repositorio.guardar(nueva("B"));

    repositorio.eliminarPorId(a.getId());

    assertThat(repositorio.buscarTodas()).extracting(Tarea::getTitulo).containsExactly("B");
  }

  @Test
  void contarPorEstado_cuentaSoloLasDeEseEstado() {
    Tarea a = repositorio.guardar(nueva("A"));
    repositorio.guardar(nueva("B"));
    a.setEstado(EstadoTarea.EN_PROGRESO);

    assertThat(repositorio.contarPorEstado(EstadoTarea.EN_PROGRESO)).isEqualTo(1);
    assertThat(repositorio.contarPorEstado(EstadoTarea.PENDIENTE)).isEqualTo(1);
    assertThat(repositorio.contarPorEstado(EstadoTarea.COMPLETADA)).isZero();
  }
}
