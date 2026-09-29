package com.example.todo.controller;

import com.example.todo.dto.CambioEstadoRequest;
import com.example.todo.dto.EstadisticasResponse;
import com.example.todo.dto.TareaRequest;
import com.example.todo.dto.TareaResponse;
import com.example.todo.model.EstadoTarea;
import com.example.todo.model.Prioridad;
import com.example.todo.model.Tarea;
import com.example.todo.service.TareaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

  private final TareaService servicio;

  public TareaController(TareaService servicio) {
    this.servicio = servicio;
  }

  @PostMapping
  public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest peticion) {
    Tarea creada = servicio.crear(peticion);
    return ResponseEntity.created(URI.create("/api/tareas/" + creada.getId()))
        .body(TareaResponse.desde(creada));
  }

  @GetMapping
  public List<TareaResponse> listar(
      @RequestParam(required = false) EstadoTarea estado,
      @RequestParam(required = false) Prioridad prioridad) {
    return servicio.listar(estado, prioridad).stream().map(TareaResponse::desde).toList();
  }

  @GetMapping("/vencidas")
  public List<TareaResponse> listarVencidas() {
    return servicio.listarVencidas().stream().map(TareaResponse::desde).toList();
  }

  @GetMapping("/{id}")
  public TareaResponse obtener(@PathVariable Long id) {
    return TareaResponse.desde(servicio.obtener(id));
  }

  @GetMapping("/estadisticas")
  public EstadisticasResponse estadisticas() {
    return servicio.estadisticas();
  }

  @PutMapping("/{id}")
  public TareaResponse actualizar(
      @PathVariable Long id, @Valid @RequestBody TareaRequest peticion) {
    return TareaResponse.desde(servicio.actualizar(id, peticion));
  }

  @PatchMapping("/{id}/estado")
  public TareaResponse cambiarEstado(
      @PathVariable Long id, @Valid @RequestBody CambioEstadoRequest peticion) {
    return TareaResponse.desde(servicio.cambiarEstado(id, peticion.estado()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    servicio.eliminar(id);
  }
}
