package com.empresa.excepciones;

public class PrecioNegativoException extends Exception {
    private static final long serialVersionUID = 1L;

    public PrecioNegativoException(String mensaje) {
        super(mensaje);
    }
}
