package com.example.todo.dto;

import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Prioridad;
import java.util.Map;

/** Número de tareas agrupadas por estado y por prioridad. */
public record EstadisticasResponse(
    Map<EstadoTarea, Long> porEstado, Map<Prioridad, Long> porPrioridad) {}
