package com.empresa.servicios;

import com.empresa.productos.Producto;
import com.empresa.pedidos.Pedido;
import com.empresa.excepciones.*;
import com.empresa.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {
    @Autowired private ProductoRepository productoRepo;
    @Autowired private PedidoRepository pedidoRepo;

    public List<Producto> obtenerTodos() { return productoRepo.findAll(); }
    
    public Producto guardarProducto(Producto p) { return productoRepo.save(p); }

    public List<Pedido> obtenerPedidosPorUsuario(int usuarioId) { return pedidoRepo.findByUsuarioId(usuarioId); }

    public Pedido procesarPedido(int usuarioId, Map<Integer, Integer> carrito) 
            throws ProductoNoEncontradoException, StockInsuficienteException, CantidadNegativaException, PedidovacioException {
        
        if (carrito == null || carrito.isEmpty()) throw new PedidovacioException("El carrito está vacío.");
        
        Pedido pedido = new Pedido(usuarioId);
        
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Producto p = productoRepo.findById(entry.getKey())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado ID: " + entry.getKey()));
            
            pedido.agregarProducto(p, entry.getValue());
            p.setStock(p.getStock() - entry.getValue());
            productoRepo.save(p); 
        }
        
        pedido.liquidarTotal();
        return pedidoRepo.save(pedido); 
    }
}