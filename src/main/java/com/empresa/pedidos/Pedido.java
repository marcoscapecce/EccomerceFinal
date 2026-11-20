package com.empresa.pedidos;

import com.empresa.productos.Producto;
import com.empresa.excepciones.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int usuarioId; 
    private String estado = "pendiente"; 
    private double costoTotal;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LineaPedido> lineas = new ArrayList<>();

    public Pedido() {}
    public Pedido(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void agregarProducto(Producto producto, int cantidad) throws StockInsuficienteException, CantidadNegativaException {
        if (cantidad <= 0) {
            throw new CantidadNegativaException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException("Stock insuficiente para: " + producto.getNombre());
        }
        lineas.add(new LineaPedido(producto, cantidad));
    }

    public void liquidarTotal() {
        this.costoTotal = lineas.stream().mapToDouble(LineaPedido::calcularSubtotal).sum();
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int id) { this.usuarioId = id; }
    public String getEstado() { return estado; }
    public void setEstado(String e) { this.estado = e; }
    public double getCostoTotal() { return costoTotal; }
    public List<LineaPedido> getLineas() { return lineas; }
}
