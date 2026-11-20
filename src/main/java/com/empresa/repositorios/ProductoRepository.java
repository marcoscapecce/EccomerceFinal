package com.empresa.repositorios;
import com.empresa.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {}
