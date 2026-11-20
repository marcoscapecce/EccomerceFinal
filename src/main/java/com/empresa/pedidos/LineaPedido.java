package com.empresa.pedidos;

import com.empresa.productos.Producto;
import jakarta.persistence.*;

@Entity
public class LineaPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    private Producto producto;
    private int cantidad;

    public LineaPedido() {}
    public LineaPedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double calcularSubtotal() {
        return producto.calcularPrecioFinal() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
}