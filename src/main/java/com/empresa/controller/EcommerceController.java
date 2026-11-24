package com.empresa.controller;

import com.empresa.productos.Producto;
import com.empresa.pedidos.Pedido;
import com.empresa.servicios.ProductoService;
import com.empresa.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.net.URI;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EcommerceController {

    @Autowired private ProductoService service;

    @GetMapping("/productos")
    public List<Producto> listar() { 
        return service.obtenerTodos(); 
    }

    @PostMapping("/productos")
    public Producto crear(@RequestBody Producto producto) throws PrecioNegativoException {
        if (producto == null) {
            throw new IllegalArgumentException("Debes enviar los datos del producto.");
        }
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria.");
        }
        if (!Double.isFinite(producto.getPrecio()) || producto.getPrecio() <= 0) {
            throw new PrecioNegativoException("El precio del producto debe ser mayor a cero.");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (!"electronica".equals(producto.getCategoria()) && !"alimentos".equals(producto.getCategoria())) {
            throw new IllegalArgumentException("La categoría debe ser electronica o alimentos.");
        }
        String urlImagen = producto.getUrlImagen();
        try {
            URI uri = URI.create(urlImagen == null ? "" : urlImagen.trim());
            if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La URL de imagen debe ser una dirección HTTP o HTTPS válida.");
        }
        return service.guardarProducto(producto); 
    }

    @PostMapping("/pedidos")
    public Pedido comprar(@RequestBody Map<Integer, Integer> carrito) 
            throws StockInsuficienteException, ProductoNoEncontradoException, CantidadNegativaException, PedidovacioException {
        return service.procesarPedido(5, carrito); 
    }

    @GetMapping("/usuarios/{id}/pedidos")
    public List<Pedido> historial(@PathVariable int id) { 
        return service.obtenerPedidosPorUsuario(id); 
    }
}