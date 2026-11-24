package com.empresa.controller;

import com.empresa.excepciones.StockInsuficienteException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {
    @Test
    void faltaDeStockSeDevuelveComoBadRequestConMensaje() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, String>> response =
            handler.handleBadRequest(new StockInsuficienteException("Stock insuficiente para el producto"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Stock insuficiente para el producto", response.getBody().get("mensaje"));
    }
}
