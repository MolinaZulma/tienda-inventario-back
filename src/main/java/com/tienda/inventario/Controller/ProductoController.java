package com.tienda.inventario.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.Model.Producto;
import com.tienda.inventario.Service.ProductoService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173")//permite que React se conecte  
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos(){
        return productoService.listarTodos();
    }

    @PostMapping("/vender/{id}") 
    public ResponseEntity<String> vender(@PathVariable Long id, @RequestParam Integer cantidad){
        try{
            productoService.realizarVenta(id, cantidad);
            return ResponseEntity.ok("Venta realizada exitosamente");
            
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }   
    
    @PostMapping("/batch")
    public ResponseEntity<?> crearVariosProductos(@RequestBody List<Producto> productos) {
        try{
            List<Producto> productosGuardados = productoService.guardarVarios(productos);
            return ResponseEntity.ok(productosGuardados);
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al guardar la lista de productos: "+ e.getMessage());
        }

    }

    @PostMapping
    public ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto){
        Producto nuevo = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok("Producto eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto producto){
        try {
            Producto productoActualizado = productoService.actualizar(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
