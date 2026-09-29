package com.example.todo.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todo.dto.ErrorRespuesta;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler manejador = new GlobalExceptionHandler();

  @Test
  void tareaNoEncontrada_devuelve404ConMensaje() {
    ResponseEntity<ErrorRespuesta> respuesta =
        manejador.manejarNoEncontrada(new TareaNoEncontradaException(42L));

    assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
    assertThat(respuesta.getBody()).isNotNull();
    assertThat(respuesta.getBody().status()).isEqualTo(404);
    assertThat(respuesta.getBody().mensaje()).contains("42");
  }

  @Test
  void reglaDeNegocio_devuelve422ConElMensajeDeLaRegla() {
    ResponseEntity<ErrorRespuesta> respuesta =
        manejador.manejarReglaNegocio(new ReglaNegocioException("Regla incumplida"));

    assertThat(respuesta.getStatusCode().value()).isEqualTo(422);
    assertThat(respuesta.getBody()).isNotNull();
    assertThat(respuesta.getBody().mensaje()).isEqualTo("Regla incumplida");
  }

  @Test
  void errorInesperado_devuelve500SinFiltrarElDetalleInterno() {
    ResponseEntity<ErrorRespuesta> respuesta =
        manejador.manejarInesperado(new IllegalStateException("password=secreto"));

    assertThat(respuesta.getStatusCode().value()).isEqualTo(500);
    assertThat(respuesta.getBody()).isNotNull();
    assertThat(respuesta.getBody().mensaje()).doesNotContain("secreto");
  }

  @Test
  void parametroFaltante_devuelve400ConElNombreDelParametro() {
    ResponseEntity<ErrorRespuesta> respuesta =
        manejador.manejarParametroFaltante(
            new MissingServletRequestParameterException("q", "String"));

    assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
    assertThat(respuesta.getBody()).isNotNull();
    assertThat(respuesta.getBody().mensaje()).contains("q");
  }
}
