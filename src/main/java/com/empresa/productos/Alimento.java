package com.empresa.productos;

import jakarta.persistence.Entity;

@Entity
public class Alimento extends Producto {
    private String fechaVencimiento;

    public Alimento() {}
    public Alimento(String nombre, String descripcion, double precio, String categoria, String urlImagen, int stock, String fechaVencimiento) throws com.empresa.excepciones.PrecioNegativoException {
        super(nombre, descripcion, precio, categoria, urlImagen, stock);
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.getPrecio() * 0.95; 
    }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String f) { this.fechaVencimiento = f; }
}