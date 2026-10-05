package com.example.todo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
public class Tarea {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;
  private String descripcion;

  @Enumerated(EnumType.STRING)
  private Prioridad prioridad;

  @Enumerated(EnumType.STRING)
  private EstadoTarea estado;

  private LocalDate fechaLimite;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaCompletada;

  /** Requerido por JPA. */
  protected Tarea() {}

  public Tarea(
      String titulo,
      String descripcion,
      Prioridad prioridad,
      LocalDate fechaLimite,
      LocalDateTime fechaCreacion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.prioridad = prioridad;
    this.fechaLimite = fechaLimite;
    this.fechaCreacion = fechaCreacion;
    this.estado = EstadoTarea.PENDIENTE;
  }

  public void actualizarDatos(
      String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.prioridad = prioridad;
    this.fechaLimite = fechaLimite;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public Prioridad getPrioridad() {
    return prioridad;
  }

  public EstadoTarea getEstado() {
    return estado;
  }

  public void setEstado(EstadoTarea estado) {
    this.estado = estado;
  }

  public LocalDate getFechaLimite() {
    return fechaLimite;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public LocalDateTime getFechaCompletada() {
    return fechaCompletada;
  }

  public void setFechaCompletada(LocalDateTime fechaCompletada) {
    this.fechaCompletada = fechaCompletada;
  }
}
