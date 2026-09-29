package com.example.todo.exception;

/** Se lanza cuando un parámetro de la petición es obligatorio pero falta o está vacío. */
public class ParametroInvalidoException extends RuntimeException {

  public ParametroInvalidoException(String mensaje) {
    super(mensaje);
  }
}
