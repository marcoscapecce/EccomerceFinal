package com.empresa.servicios;

import com.empresa.productos.Producto;
import com.empresa.pedidos.Pedido;
import com.empresa.excepciones.*;
import com.empresa.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class ProductoService {
    @Autowired private ProductoRepository productoRepo;
    @Autowired private PedidoRepository pedidoRepo;

    public List<Producto> obtenerTodos() { return productoRepo.findAll(); }
    
    public Producto guardarProducto(Producto p) { return productoRepo.save(p); }

    public List<Pedido> obtenerPedidosPorUsuario(int usuarioId) { return pedidoRepo.findByUsuarioId(usuarioId); }

    @Transactional(rollbackFor = Exception.class)
    public Pedido procesarPedido(int usuarioId, Map<Integer, Integer> carrito)
            throws ProductoNoEncontradoException, StockInsuficienteException, CantidadNegativaException, PedidovacioException {
        
        if (carrito == null || carrito.isEmpty()) throw new PedidovacioException("El carrito está vacío.");
        
        Pedido pedido = new Pedido(usuarioId);
        List<Map.Entry<Integer, Integer>> items = new ArrayList<>(carrito.entrySet());
        if (items.stream().anyMatch(item -> item.getKey() == null || item.getKey() <= 0)) {
            throw new ProductoNoEncontradoException("El carrito contiene un identificador de producto inválido.");
        }
        items.sort(Comparator.comparing(Map.Entry::getKey));

        List<Producto> productos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : items) {
            if (entry.getValue() == null) {
                throw new CantidadNegativaException("La cantidad debe ser mayor a cero.");
            }
            Producto p = productoRepo.findByIdForUpdate(entry.getKey())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado ID: " + entry.getKey()));
            pedido.agregarProducto(p, entry.getValue());
            productos.add(p);
        }

        for (int i = 0; i < items.size(); i++) {
            Producto producto = productos.get(i);
            producto.setStock(producto.getStock() - items.get(i).getValue());
        }

        pedido.liquidarTotal();
        return pedidoRepo.save(pedido); 
    }
}