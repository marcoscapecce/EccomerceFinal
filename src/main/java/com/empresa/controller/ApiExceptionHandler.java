package com.empresa.controller;

import com.empresa.excepciones.CantidadNegativaException;
import com.empresa.excepciones.PedidovacioException;
import com.empresa.excepciones.PrecioNegativoException;
import com.empresa.excepciones.ProductoNoEncontradoException;
import com.empresa.excepciones.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
        StockInsuficienteException.class,
        ProductoNoEncontradoException.class,
        CantidadNegativaException.class,
        PedidovacioException.class,
        PrecioNegativoException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("mensaje", "El cuerpo de la solicitud no tiene un formato válido."));
    }
}
