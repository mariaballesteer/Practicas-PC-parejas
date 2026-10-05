package com.example.todo.repository;

import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaJpaRepository extends JpaRepository<Tarea, Long> {

  long countByEstado(EstadoTarea estado);
}
