package com.empresa.productos;

import jakarta.persistence.Entity;

@Entity
public class Electronico extends Producto {
    private int mesesGarantia;

    public Electronico() {}
    public Electronico(String nombre, String descripcion, double precio, String categoria, String urlImagen, int stock, int mesesGarantia) throws com.empresa.excepciones.PrecioNegativoException {
        super(nombre, descripcion, precio, categoria, urlImagen, stock);
        this.mesesGarantia = mesesGarantia;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.getPrecio() * 1.10; 
    }

    public int getMesesGarantia() { return mesesGarantia; }
    public void setMesesGarantia(int m) { this.mesesGarantia = m; }
}