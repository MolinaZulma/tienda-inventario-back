package com.tienda.inventario.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.inventario.Model.MovimientoInventario;
import com.tienda.inventario.Model.Producto;
import com.tienda.inventario.Model.Proveedor;
import com.tienda.inventario.Repository.MovimientoRepository;
import com.tienda.inventario.Repository.ProductoRepository;
import com.tienda.inventario.Repository.ProveedorRepository;

import jakarta.transaction.Transactional;

@Service
public class CompraService {
    @Autowired private ProductoRepository productoRepository;
    @Autowired private MovimientoRepository movimientoRepository;
    @Autowired private ProveedorRepository proveedorRepository;

    @Transactional
    public void registrarEntrada(Long productoId, Integer cantidad, Long proveedorId) {
       
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

   
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

     
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo("ENTRADA");
        movimiento.setMotivo("COMPRA A PROVEEDOR: " + proveedor.getNombre());
        movimiento.setFecha(LocalDateTime.now());
        
        movimientoRepository.save(movimiento);

    }
    
}
