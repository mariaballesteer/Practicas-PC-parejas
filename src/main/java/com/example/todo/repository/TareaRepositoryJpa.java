package com.example.todo.repository;

import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Tarea;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/** Persistencia en PostgreSQL mediante Spring Data JPA (Hibernate). */
@Repository
public class TareaRepositoryJpa implements TareaRepository {

  private final TareaJpaRepository jpa;

  public TareaRepositoryJpa(TareaJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Tarea guardar(Tarea tarea) {
    return jpa.save(tarea);
  }

  @Override
  public Optional<Tarea> buscarPorId(Long id) {
    return jpa.findById(id);
  }

  @Override
  public List<Tarea> buscarTodas() {
    return jpa.findAll(Sort.by("id"));
  }

  @Override
  public long contarPorEstado(EstadoTarea estado) {
    return jpa.countByEstado(estado);
  }

  @Override
  public void eliminarPorId(Long id) {
    jpa.deleteById(id);
  }
}
