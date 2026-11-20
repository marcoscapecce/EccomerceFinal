package com.empresa.Ecommerce;

import com.empresa.productos.Producto;
import com.empresa.pedidos.Pedido;
import com.empresa.servicios.ProductoService;
import com.empresa.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class Main implements CommandLineRunner {

    @Autowired
    private ProductoService service;

    @Override
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
        
            System.out.println("=== INTERFAZ DE CONSOLA CORE (CONECTADA A SERVICIO JPA) ===");
        
            boolean continuar = true;
            while (continuar) {
                try {
                    System.out.println("\n1. Ver Catálogo en Base de Datos | 2. Simular Compra de Carrito | 3. Salir");
                    System.out.print("Opción: ");
                
                    int opcion = Integer.parseInt(scanner.nextLine());

                    if (opcion == 1) {
                        System.out.println("\nProductos en MySQL:");
                        for (Producto p : service.obtenerTodos()) {
                            System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " | Stock: " + p.getStock() + " | Precio: $" + p.calcularPrecioFinal());
                        }
                    } else if (opcion == 2) {
                        Map<Integer, Integer> carritoSimulado = new HashMap<>();
                    
                        System.out.print("Ingrese ID del producto que quiere comprar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                    
                        System.out.print("Cantidad requerida: ");
                        int cant = Integer.parseInt(scanner.nextLine());
                    
                        carritoSimulado.put(id, cant);

                        try {
                            System.out.println("Enviando carrito al servicio de procesamiento...");
                            Pedido resultado = service.procesarPedido(5, carritoSimulado);
                            System.out.println("¡Compra procesada con éxito!");
                            System.out.println("Pedido ID: #" + resultado.getId() + " | Estado: " + resultado.getEstado() + " | Total Liquidado: $" + resultado.getCostoTotal());
                        } catch (ProductoNoEncontradoException | StockInsuficienteException | CantidadNegativaException | PedidovacioException e) {
                            System.out.println("EXCEPCIÓN DE NEGOCIO CAPTURADA -> " + e.getMessage());
                        }
                    } else if (opcion == 3) {
                        System.out.println("Cerrando interfaz de consola.");
                        continuar = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("¡ERROR DE ENTRADA! Debes ingresar un número entero válido en la consola.");
                }
            }
        }
    }
}