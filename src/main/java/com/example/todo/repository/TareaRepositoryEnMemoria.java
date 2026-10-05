package com.example.todo.repository;

import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Tarea;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/** Persistencia en memoria (ya no es un bean): se usa solo como doble en los tests. */
public class TareaRepositoryEnMemoria implements TareaRepository {

  // ConcurrentSkipListMap mantiene las tareas ordenadas por id
  private final Map<Long, Tarea> tareas = new ConcurrentSkipListMap<>();
  private final AtomicLong secuencia = new AtomicLong(0);

  @Override
  public Tarea guardar(Tarea tarea) {
    if (tarea.getId() == null) {
      tarea.setId(secuencia.incrementAndGet());
    }
    tareas.put(tarea.getId(), tarea);
    return tarea;
  }

  @Override
  public Optional<Tarea> buscarPorId(Long id) {
    return Optional.ofNullable(tareas.get(id));
  }

  @Override
  public List<Tarea> buscarTodas() {
    return new ArrayList<>(tareas.values());
  }

  @Override
  public long contarPorEstado(EstadoTarea estado) {
    return tareas.values().stream().filter(t -> t.getEstado() == estado).count();
  }

  @Override
  public void eliminarPorId(Long id) {
    tareas.remove(id);
  }
}
