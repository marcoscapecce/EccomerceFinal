package com.empresa.excepciones;

public class PedidovacioException extends Exception {
    private static final long serialVersionUID = 1L;

    public PedidovacioException(String mensaje) {
        super(mensaje);
    }
}