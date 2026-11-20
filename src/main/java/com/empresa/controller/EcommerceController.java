package com.empresa.controller;

import com.empresa.productos.Producto;
import com.empresa.pedidos.Pedido;
import com.empresa.servicios.ProductoService;
import com.empresa.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
        if (producto.getPrecio() <= 0) {
            throw new PrecioNegativoException("El precio del producto debe ser mayor a cero.");
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