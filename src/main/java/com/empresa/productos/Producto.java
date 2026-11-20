package com.empresa.productos;

import jakarta.persistence.*;
import com.empresa.excepciones.PrecioNegativoException;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private String urlImagen;
    private int stock;

    public Producto() {}

    public Producto(String nombre, String descripcion, double precio, String categoria, String urlImagen, int stock) throws PrecioNegativoException {
        if (precio <= 0) {
            throw new PrecioNegativoException("El precio debe ser mayor a cero.");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.urlImagen = urlImagen;
        this.stock = stock;
    }

    public double calcularPrecioFinal() {
        return this.precio;
    }

    public int getId() { 
    	return id; 
    }
    public void setId(int id) { 
    	this.id = id; 
    }
    public String getNombre() { 
    	return nombre; 
    }
    public void setNombre(String nombre) { 
    	this.nombre = nombre; 
    }
    public String getDescripcion() { 
    	return descripcion; 
    }
    public void setDescripcion(String d) { 
    	this.descripcion = d; 
    }
    public double getPrecio() { 
    	return precio; 
    }
    public void setPrecio(double p) { 
    	this.precio = p; 
    }
    public String getCategoria() { 
    	return categoria; 
    }
    public void setCategoria(String c) { 
    	this.categoria = c; 
    }
    public String getUrlImagen() { 
    	return urlImagen; 
    }
    public void setUrlImagen(String url) { 
    	this.urlImagen = url; 
    }
    public int getStock() { 
    	return stock; 
    }
    public void setStock(int s) { 
    	this.stock = s; 
    }
}