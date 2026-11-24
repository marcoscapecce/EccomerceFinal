package com.empresa.servicios;

import com.empresa.excepciones.StockInsuficienteException;
import com.empresa.pedidos.Pedido;
import com.empresa.productos.Producto;
import com.empresa.repositorios.PedidoRepository;
import com.empresa.repositorios.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepo;
    @Mock
    private PedidoRepository pedidoRepo;
    @InjectMocks
    private ProductoService service;

    @Test
    void pedidoConStockInsuficienteNoDescuentaNingunProducto() {
        Producto disponible = producto(10);
        Producto sinStockSuficiente = producto(2);
        when(productoRepo.findByIdForUpdate(1)).thenReturn(Optional.of(disponible));
        when(productoRepo.findByIdForUpdate(2)).thenReturn(Optional.of(sinStockSuficiente));

        assertThrows(StockInsuficienteException.class,
            () -> service.procesarPedido(5, Map.of(1, 1, 2, 3)));

        assertEquals(10, disponible.getStock());
        assertEquals(2, sinStockSuficiente.getStock());
        verify(pedidoRepo, never()).save(any(Pedido.class));
    }

    @Test
    void pedidoValidoDescuentaStockYRegistraFecha() throws Exception {
        Producto producto = producto(5);
        producto.setPrecio(12.50);
        when(productoRepo.findByIdForUpdate(1)).thenReturn(Optional.of(producto));
        when(pedidoRepo.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido pedido = service.procesarPedido(5, Map.of(1, 2));

        assertEquals(3, producto.getStock());
        assertEquals(25.0, pedido.getCostoTotal());
        assertEquals("pendiente", pedido.getEstado());
        assertNotNull(pedido.getFechaCreacion());
    }

    private Producto producto(int stock) {
        Producto producto = new Producto();
        producto.setId(stock == 10 ? 1 : 2);
        producto.setNombre("Producto de prueba");
        producto.setPrecio(10.0);
        producto.setStock(stock);
        return producto;
    }
}
