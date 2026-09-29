package com.example.todo.exception;

import com.example.todo.dto.ErrorRespuesta;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Manejo centralizado de errores: nunca se devuelve una traza al cliente. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(TareaNoEncontradaException.class)
  public ResponseEntity<ErrorRespuesta> manejarNoEncontrada(TareaNoEncontradaException ex) {
    return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
  }

  @ExceptionHandler(ReglaNegocioException.class)
  public ResponseEntity<ErrorRespuesta> manejarReglaNegocio(ReglaNegocioException ex) {
    return construir(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorRespuesta> manejarValidacion(MethodArgumentNotValidException ex) {
    List<String> detalles =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
    return construir(HttpStatus.BAD_REQUEST, "La petición contiene datos no válidos", detalles);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorRespuesta> manejarJsonIlegible(HttpMessageNotReadableException ex) {
    return construir(
        HttpStatus.BAD_REQUEST,
        "El cuerpo de la petición no es un JSON válido o contiene valores incorrectos "
            + "(revisa fechas con formato AAAA-MM-DD y los valores de los enums)",
        List.of());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorRespuesta> manejarTipoIncorrecto(
      MethodArgumentTypeMismatchException ex) {
    return construir(
        HttpStatus.BAD_REQUEST,
        "Valor '" + ex.getValue() + "' no válido para el parámetro '" + ex.getName() + "'",
        List.of());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorRespuesta> manejarRutaInexistente(NoResourceFoundException ex) {
    return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", List.of());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorRespuesta> manejarMetodoNoPermitido(
      HttpRequestMethodNotSupportedException ex) {
    return construir(
        HttpStatus.METHOD_NOT_ALLOWED,
        "Método " + ex.getMethod() + " no permitido en esta ruta",
        List.of());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorRespuesta> manejarMediaType(HttpMediaTypeNotSupportedException ex) {
    return construir(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "Tipo de contenido no soportado; usa application/json",
        List.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorRespuesta> manejarInesperado(Exception ex) {
    log.error("Error no controlado", ex);
    return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", List.of());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorRespuesta> manejarParametroFaltante(
      MissingServletRequestParameterException ex) {
    return construir(
        HttpStatus.BAD_REQUEST,
        "Falta el parámetro obligatorio '" + ex.getParameterName() + "'",
        List.of());
  }

  @ExceptionHandler(ParametroInvalidoException.class)
  public ResponseEntity<ErrorRespuesta> manejarParametroInvalido(ParametroInvalidoException ex) {
    return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
  }

  private ResponseEntity<ErrorRespuesta> construir(
      HttpStatus status, String mensaje, List<String> detalles) {
    ErrorRespuesta cuerpo =
        new ErrorRespuesta(
            LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, detalles);
    return ResponseEntity.status(status).body(cuerpo);
  }
}
