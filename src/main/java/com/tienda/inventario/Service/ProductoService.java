package com.tienda.inventario.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.inventario.Model.Producto;
import com.tienda.inventario.Repository.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    //método para registrar producto nuevo
    public Producto guardarProducto(Producto producto){
        return productoRepository.save(producto);
    }

    public List<Producto> guardarVarios(List<Producto> productos){
        return productoRepository.saveAll(productos);
    }

    //vender producto con validación
    public void realizarVenta(Long productoId, Integer cantidadAVender){
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado:"));
        if(producto.getStock() < cantidadAVender){
            throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
        }

        //Restamos el stock y guardamos
        producto.setStock(producto.getStock() - cantidadAVender);
        productoRepository.save(producto);
    }

    public List<Producto> listarTodos(){
        return productoRepository.findAll();
    }

    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con el id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public Producto actualizar(Long id, Producto producto){
        if(!productoRepository.existsById(id)){
            throw new RuntimeException("Producto no encontrado con el id: " + id);
        }
        producto.setId(id);
        return productoRepository.save(producto);
    }

}
