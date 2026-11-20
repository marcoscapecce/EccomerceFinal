package com.empresa.excepciones;

public class CantidadNegativaException extends Exception {
    private static final long serialVersionUID = 1L;

    public CantidadNegativaException(String mensaje) {
        super(mensaje);
    }
}
